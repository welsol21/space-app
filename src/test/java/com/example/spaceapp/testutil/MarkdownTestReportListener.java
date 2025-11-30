package com.example.spaceapp.testutil;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MarkdownTestReportListener implements TestExecutionListener {

    private static final class TestResultRow {
        final String className;
        final String method;
        volatile String status; // PASSED / FAILED / ABORTED
        volatile String failure;

        TestResultRow(String className, String method) {
            this.className = className;
            this.method = method;
        }
    }

    private final Map<String, TestResultRow> resultsByUniqueId = new ConcurrentHashMap<>();
    private volatile OffsetDateTime startedAt;

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        startedAt = OffsetDateTime.now();
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (!testIdentifier.isTest()) return;

        String className = "UnknownClass";
        String methodName = testIdentifier.getDisplayName();

        Optional<?> srcOpt = testIdentifier.getSource();
        if (srcOpt.isPresent()) {
            Object src = srcOpt.get();
            if (src instanceof MethodSource ms) {
                className = ms.getClassName();
                methodName = ms.getMethodName();
            } else if (src instanceof ClassSource cs) {
                className = cs.getClassName();
                methodName = testIdentifier.getDisplayName();
            }
        }

        final String cName = className;
        final String mName = methodName;
        TestResultRow row = resultsByUniqueId.computeIfAbsent(
                testIdentifier.getUniqueId(),
                k -> new TestResultRow(cName, mName)
        );

        row.status = switch (testExecutionResult.getStatus()) {
            case SUCCESSFUL -> "PASSED";
            case ABORTED -> "ABORTED";
            case FAILED -> "FAILED";
        };

        row.failure = testExecutionResult.getThrowable()
                .map(t -> {
                    String msg = t.getClass().getSimpleName() + (t.getMessage() != null ? (": " + t.getMessage()) : "");
                    // Keep report readable; avoid mega stacktraces.
                    if (msg.length() > 400) msg = msg.substring(0, 400) + "…";
                    return msg;
                })
                .orElse(null);
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        List<TestResultRow> rows = new ArrayList<>(resultsByUniqueId.values());
        rows.sort(Comparator
                .comparing((TestResultRow r) -> r.className)
                .thenComparing(r -> r.method));

        Map<String, List<TestResultRow>> byClass = new LinkedHashMap<>();
        for (TestResultRow r : rows) {
            byClass.computeIfAbsent(r.className, k -> new ArrayList<>()).add(r);
        }

        StringBuilder md = new StringBuilder();
        md.append("# Test Report\n\n");
        md.append("- Generated: ").append(OffsetDateTime.now()).append("\n");
        if (startedAt != null) md.append("- Started: ").append(startedAt).append("\n");
        md.append("- Total tests: ").append(rows.size()).append("\n\n");

        for (Map.Entry<String, List<TestResultRow>> entry : byClass.entrySet()) {
            md.append("## ").append(entry.getKey()).append("\n\n");
            for (TestResultRow r : entry.getValue()) {
                String icon = switch (r.status) {
                case "PASSED" -> "✅";
                case "FAILED" -> "❌";
                default -> "⚪";
            };
            String friendly = r.method != null ? r.method.replace('_', ' ') : "";
            String expected = "";
            int idx = friendly.toLowerCase().indexOf("should");
            if (idx >= 0) {
                expected = friendly.substring(idx + "should".length()).trim();
            }
            String risks = "Unexpected status/exception; validation mismatch; missing resource; security rule misconfigured";

            md.append("- ").append(icon).append(" `").append(r.method).append("`");
            if ("FAILED".equals(r.status) && r.failure != null && !r.failure.isBlank()) {
                md.append(" — ").append(r.failure.replace("\n", " "));
            }
            if (!friendly.isBlank()) {
                md.append(" — ").append(friendly);
            }
            if (!expected.isBlank()) {
                md.append(" | Expected: ").append(expected);
            }
            md.append(" | Potential errors: ").append(risks);
            md.append("\n");
            }
            md.append("\n");
        }

        // Write to target/ and optionally project root.
        writeReportSafe("target/test-report.md", md.toString());
        writeReportSafe("test-report.md", md.toString());
    }

    private void writeReportSafe(String relativePath, String content) {
        try {
            Path out = Path.of(System.getProperty("user.dir")).resolve(relativePath);
            Files.createDirectories(out.getParent() != null ? out.getParent() : out.toAbsolutePath().getParent());
            Files.writeString(out, content, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // Never fail the build because of reporting.
        } catch (Exception ignored) {
        }
    }
}
