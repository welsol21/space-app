package com.example.spaceapp.testutil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtil() {}

    public static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize to JSON", e);
        }
    }

    public static long readLongField(String json, String fieldName) {
        try {
            JsonNode node = MAPPER.readTree(json).get(fieldName);
            if (node == null || !node.canConvertToLong()) {
                throw new IllegalArgumentException("Field '" + fieldName + "' is missing or not a long");
            }
            return node.asLong();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON field '" + fieldName + "'", e);
        }
    }
}
