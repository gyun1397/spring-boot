package com.common.converter.serializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.common.util.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@SuppressWarnings({"rawtypes", "unchecked"})
public class CustomDateSerializer extends StdSerializer<Date> {
    private static final long serialVersionUID = -8617084269080524830L;
    private static final SimpleDateFormat formatter = new SimpleDateFormat(DateUtil.TIMESTAMP, Locale.KOREA);

    public CustomDateSerializer() {
        this(null); 
    }

    public CustomDateSerializer(Class t) {
        super(t);
    }

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
        gen.writeString(formatter.format(value));
    }
}