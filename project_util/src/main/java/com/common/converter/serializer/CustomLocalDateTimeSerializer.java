package com.common.converter.serializer;

import java.io.IOException;
import java.time.LocalDateTime;
import com.common.util.DateTimeUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CustomLocalDateTimeSerializer extends StdSerializer<LocalDateTime> {
    private static final long serialVersionUID = -189394595692846558L;

    public CustomLocalDateTimeSerializer() {
        this(null);
    }

    public CustomLocalDateTimeSerializer(Class t) {
        super(t);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
        gen.writeString(DateTimeUtil.convertDateToString(value));
    }
}