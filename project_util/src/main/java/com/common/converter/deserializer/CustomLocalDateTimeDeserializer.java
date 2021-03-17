package com.common.converter.deserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import com.common.util.DateTimeUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("rawtypes")
public class CustomLocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    private static final long serialVersionUID = 1826512564393843679L;

    public CustomLocalDateTimeDeserializer() {
        this(null); 
    }

    public CustomLocalDateTimeDeserializer(Class t) {
        super(t);
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return DateTimeUtil.convertLocalDateTime(p.getText());
    }

}