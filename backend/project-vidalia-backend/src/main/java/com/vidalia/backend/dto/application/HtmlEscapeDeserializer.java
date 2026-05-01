package com.vidalia.backend.dto.application;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;

public class HtmlEscapeDeserializer extends StdDeserializer<String> {
    public HtmlEscapeDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value != null) {
            value = value.trim();
            value = HtmlUtils.htmlEscape(value);
        }
        return value;
    }
}

