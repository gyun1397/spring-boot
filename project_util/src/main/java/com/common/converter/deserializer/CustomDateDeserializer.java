package com.common.converter.deserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import com.common.util.DateUtil.DATE_FORMAT;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers.DateDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

@SuppressWarnings("rawtypes") 
public class CustomDateDeserializer extends StdDeserializer<Date> {
    private static final long serialVersionUID = 7951713739185870804L;
    
    @Autowired
    private DateDeserializer dateDeserializer;

    public CustomDateDeserializer() {
        this(null); 
    }

    public CustomDateDeserializer(Class t) {
        super(t);
    }

    @Override
    public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        DATE_FORMAT dateFormat = DATE_FORMAT.getDateFormatByDate(p.getText());
        if (dateFormat == null) {
            if (dateDeserializer == null) {
                dateDeserializer = new DateDeserializer();
            }
            return dateDeserializer.deserialize(p, ctxt);
        }
        if ("date".equals(dateFormat.getType())) {
            LocalDate localDate = LocalDate.parse(p.getText(), DateTimeFormatter.ofPattern(dateFormat.getFormat()));
            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } else {
            LocalDateTime localDateTime = LocalDateTime.parse(p.getText(), DateTimeFormatter.ofPattern(dateFormat.getFormat()));
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }
    }

}