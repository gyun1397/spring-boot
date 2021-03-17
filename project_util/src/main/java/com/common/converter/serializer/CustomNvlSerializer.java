package com.common.converter.serializer;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CustomNvlSerializer extends StdSerializer<Object> {
    
    private static final long serialVersionUID = 7125074601140700098L;

    public CustomNvlSerializer() {
        this(null);
    }
 
    public CustomNvlSerializer(Class t) {
        super(t);
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString("");
    }
    
    
}