package com.infogain.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class PayloadUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String createOperandPayload(Object operand1, Object operand2) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("firstOperand", operand1);
            requestBody.put("secondOperand", operand2);
            return MAPPER.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize request payload", e);
        }
    }
}
