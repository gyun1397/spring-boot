package com.common.converter;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import com.common.util.DateTimeUtil;
import com.common.util.DateUtil.DATE_FORMAT;

public class CustomConverter implements Converter {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object convert(Class type, Object value) {
        if (value == null) {
            return null;
        } else if (type == Date.class) {
            return convertToDate(type, value);
        } else if (type == Timestamp.class) {
            return convertToTimestamp(type, value);
        } else if (type == LocalDateTime.class) {
            return convertToLocalDateTime(type, value);
        } else if (type == LocalDate.class) {
            return convertToLocalDate(type, value);
        }
        throw new ConversionException("Could not convert " +
                value.getClass().getName() + " to " +
                type.getName());
    }

    protected Object convertToDate(Class<?> type, Object value) {
        if (value instanceof String) {
            try {
                DateFormat formater = DATE_FORMAT.getFormater((String) value);
                if (formater != null) {
                    return formater.parse((String) value);
                }
            } catch (Exception e) {
                throw new ConversionException("Error converting String to Date");
            }
        } else if (value instanceof Date) {
            return value;
        } else if (value instanceof java.sql.Date) {
            return new Date(((java.sql.Date) value).getTime());
        }
        throw new ConversionException("Could not convert " +
                value.getClass().getName() + " to " +
                type.getName());
    }

    protected Object convertToTimestamp(Class<?> type, Object value) {
        if (value instanceof String) {
            try {
                DateFormat formater = DATE_FORMAT.getFormater((String) value);
                if (formater != null) {
                    return new Timestamp(formater.parse((String) value).getTime());
                }
            } catch (Exception e) {
                throw new ConversionException("Error converting String to TimeStamp");
            }
        } else if (value instanceof Timestamp) {
            return value;
        }
        throw new ConversionException("Could not convert " +
                value.getClass().getName() + " to " +
                type.getName());
    }
    
    protected Object convertToLocalDateTime(Class<?> type, Object value) {
        try {
            return DateTimeUtil.convertLocalDateTime(value);
        } catch (Exception e) {
            throw new ConversionException("Error converting String to LocalDateTime");
        }
    }
    
    protected Object convertToLocalDate(Class<?> type, Object value) {
        try {
            return DateTimeUtil.convertLocalDate(value);
        } catch (Exception e) {
            throw new ConversionException("Error converting String to LocalDate");
        }
    }
}
