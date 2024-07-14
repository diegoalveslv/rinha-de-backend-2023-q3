package com.example.rinhadebackend2023q3.infra;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.io.IOException;

public class StrictStringDeserializer extends StringDeserializer {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.currentToken();
        if (token.isBoolean()
                || token.isNumeric()
                || !token.toString().equalsIgnoreCase("VALUE_STRING")) {
            ctxt.reportInputMismatch(String.class, "%s is not a `String` value!", token.toString());
            return null;
        }
        return super.deserialize(p, ctxt);
    }
}
