package com.common.converter.deserializer;

import java.io.IOException;
import java.time.LocalDate;
import com.common.util.DateTimeUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("rawtypes")
public class CustomLocalDateDeserializer extends StdDeserializer<LocalDate> {
    private static final long serialVersionUID = -1521792270606575309L;

    public CustomLocalDateDeserializer() {
        this(null);
    }

    public CustomLocalDateDeserializer(Class t) {
        super(t);
    }

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return DateTimeUtil.convertLocalDate(p.getText());
    }
}