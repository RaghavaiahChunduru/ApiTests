package com.infogain.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class InfinitySerializer extends JsonSerializer<Double> {
    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == Double.POSITIVE_INFINITY) {
            gen.writeNumber(Double.POSITIVE_INFINITY); // Keep it as a numeric type
        } else if (value == Double.NEGATIVE_INFINITY) {
            gen.writeNumber(Double.NEGATIVE_INFINITY); // Keep it as a numeric type
        } else {
            gen.writeNumber(value); // Handle normal double values
        }
    }
}
