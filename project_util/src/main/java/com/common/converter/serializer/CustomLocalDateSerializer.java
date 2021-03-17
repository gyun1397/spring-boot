package com.common.converter.serializer;

import java.io.IOException;
import java.time.LocalDate;
import com.common.util.DateTimeUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CustomLocalDateSerializer extends StdSerializer<LocalDate> {
    private static final long serialVersionUID = -1222552399462306865L;

    public CustomLocalDateSerializer() {
        this(null);
    }

    public CustomLocalDateSerializer(Class t) {
        super(t);
    }

    @Override
    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
        gen.writeString(DateTimeUtil.convertDateToString(value));
    }

    
}