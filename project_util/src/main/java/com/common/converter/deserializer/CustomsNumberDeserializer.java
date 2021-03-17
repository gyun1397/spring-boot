package com.common.converter.deserializer;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("rawtypes")
public class CustomsNumberDeserializer  extends StdDeserializer<Number> {
    
    private static final long serialVersionUID = -8290208424612382352L;

    public CustomsNumberDeserializer() {
        this(null);
    }
 
    public CustomsNumberDeserializer(Class t) {
        super(t);
    }
    
    @Override
    public Number deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return p.getNumberValue() == null ? 0 : p.getNumberValue();
    }
    
}
