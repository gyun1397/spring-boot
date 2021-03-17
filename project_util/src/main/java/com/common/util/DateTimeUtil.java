package com.common.util;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import lombok.Getter;

public class DateTimeUtil {

    // 날짜유형 정규식
    private static final String DATE_REG = "([0-9]){4}([-]|[/])?([0][0-9]|[1][0-2])([-]|[/])?([0-2][0-9]|[3][0-1])";
    private static final String TIME_REG = "([T]|[ ])(([0-1][0-9]|[2][0-3])([:]([0-5][0-9])([:]([0-5][0-9])([.][0-9]{3})?)?)?)";
    private static final String STR_DATE_TIME_REG = "([0-9]){4}([0][0-9]|[1][0-2])([0-2][0-9]|[3][0-1])(([0-1][0-9]|[2][0-3])((([0-5][0-9]))((([0-5][0-9]))(([0-9]{3}))?)?)?)";
    private static final String OFFSET_REG = "([+]([0-1][0-9]|[2][0-3])([:]?([0-5][0-9]))?|[Z])";
    // 날짜 기본 포맷
    // public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ";
    public static final String      DEFAULT_DATETIME_FORMAT    = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String      DEFAULT_OFFSETTIME_FORMAT  = "HH:mm:ss.SSSZ";
    public static DateTimeFormatter zonedDateFormatter         = getZonedDateTimeFormatter();
    public static DateTimeFormatter localDateTimeFormatter     = getLocalDateTimeFormatter();
    public static DateTimeFormatter localDateFormatter         = getLocalDateFormatter();
    public static DateTimeFormatter offsetTimeFormatter        = getOffsetTimeFormatter();
    public static DateTimeFormatter localTimeFormatter         = getLocalTimeFormatter();
    public static DateTimeFormatter defaultDateTimeFormatter   = defaultDateTimeFormatter();
    public static DateTimeFormatter defaultOffsetTimeFormatter = defaultOffsetTimeFormatter();
    public static DateTimeFormatter simpleStringDateFormatter  = simpleStringDateFormatter();

    public static LocalDateTime setTime(LocalDateTime localDateTime, Integer hour, Integer minute, Integer second) {
        return localDateTime.withHour(hour).withMinute(minute).withSecond(second);
    }

    public static LocalDateTime setTime(LocalDate localDate, Integer hour, Integer minute, Integer second) {
        return localDate.atTime(hour, minute, second);
    }

    /**
     * 시,분,초를 모두 최소치로 초기화한다. (LocalDate/LocalDateTime)
     */
    public static LocalDateTime minimizedLocalDateTime(LocalDateTime localDateTime) {
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MIN);
    }

    public static LocalDateTime minimizedLocalDateTime(LocalDate localDate) {
        return LocalDateTime.of(localDate, LocalTime.MIN);
    }

    public static LocalDateTime minimizedLocalDateTime(Date date) {
        return LocalDateTime.of(convertLocalDate(date), LocalTime.MIN);
    }

    public static LocalDateTime minimizedLocalDateTime(Timestamp timestamp) {
        return LocalDateTime.of(convertLocalDate(timestamp), LocalTime.MIN);
    }

    public static LocalDateTime minimizedLocalDateTime(String strDate) {
        return LocalDateTime.of(convertLocalDate(strDate), LocalTime.MIN);
    }

    public static LocalDateTime minimizedLocalDateTime(Long time) {
        return LocalDateTime.of(convertLocalDate(time), LocalTime.MIN);
    }

    public static LocalDateTime minimizedLocalDateTime(Object obj) {
        if (obj instanceof LocalDateTime) {
            return minimizedLocalDateTime((LocalDateTime) obj);
        } else if (obj instanceof LocalDate) {
            return minimizedLocalDateTime((LocalDate) obj);
        } else if (obj instanceof Timestamp) {
            return minimizedLocalDateTime((Timestamp) obj);
        } else if (obj instanceof Date) {
            return minimizedLocalDateTime((Date) obj);
        } else if (obj instanceof String) {
            return minimizedLocalDateTime((String) obj);
        } else if (obj instanceof Long) {
            return minimizedLocalDateTime((Long) obj);
        }
        return minimizedLocalDateTime(obj.toString());
    }

    public static LocalDate minimizedLocalDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }

    public static LocalDate minimizedLocalDate(Object obj) {
        if (obj instanceof LocalDate) {
            return (LocalDate) obj;
        } else if (obj instanceof LocalDateTime) {
            return minimizedLocalDate((LocalDateTime) obj);
        } else if (obj instanceof Date) {
            return convertLocalDate((Date) obj);
        } else if (obj instanceof Timestamp) {
            return convertLocalDate((Timestamp) obj);
        } else if (obj instanceof String) {
            return convertLocalDate((String) obj);
        } else if (obj instanceof Long) {
            return convertLocalDate((Long) obj);
        }
        return convertLocalDate(obj.toString());
    }

    /**
     * 시,분,초를 모두 최소치로 초기화한다. (LocalDate/LocalDateTime)
     */
    public static LocalDateTime maximizedLocalDateTime(LocalDateTime localDateTime) {
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MIN);
    }

    public static LocalDateTime maximizedLocalDateTime(LocalDate localDate) {
        return LocalDateTime.of(localDate, LocalTime.MIN);
    }

    public static LocalDateTime maximizedLocalDateTime(Date date) {
        return LocalDateTime.of(convertLocalDate(date), LocalTime.MAX);
    }

    public static LocalDateTime maximizedLocalDateTime(Timestamp timestamp) {
        return LocalDateTime.of(convertLocalDate(timestamp), LocalTime.MAX);
    }

    public static LocalDateTime maximizedLocalDateTime(String strDate) {
        return LocalDateTime.of(convertLocalDate(strDate), LocalTime.MAX);
    }

    public static LocalDateTime maximizedLocalDateTime(Long time) {
        return LocalDateTime.of(convertLocalDate(time), LocalTime.MAX);
    }

    public static LocalDateTime maximizedLocalDateTime(Object obj) {
        if (obj instanceof LocalDateTime) {
            return maximizedLocalDateTime((LocalDateTime) obj);
        } else if (obj instanceof LocalDate) {
            return maximizedLocalDateTime((LocalDate) obj);
        } else if (obj instanceof Timestamp) {
            return maximizedLocalDateTime((Timestamp) obj);
        } else if (obj instanceof Date) {
            return maximizedLocalDateTime((Date) obj);
        } else if (obj instanceof String) {
            return maximizedLocalDateTime((String) obj);
        } else if (obj instanceof Long) {
            return maximizedLocalDateTime((Long) obj);
        }
        return maximizedLocalDateTime(obj.toString());
    }

    public static LocalDate maximizedLocalDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }

    public static LocalDate maximizedLocalDate(Object obj) {
        if (obj instanceof LocalDate) {
            return (LocalDate) obj;
        } else if (obj instanceof LocalDateTime) {
            return maximizedLocalDate((LocalDateTime) obj);
        } else if (obj instanceof Date) {
            return convertLocalDate((Date) obj);
        } else if (obj instanceof Timestamp) {
            return convertLocalDate((Timestamp) obj);
        } else if (obj instanceof String) {
            return convertLocalDate((String) obj);
        } else if (obj instanceof Long) {
            return convertLocalDate((Long) obj);
        }
        return convertLocalDate(obj.toString());
    }

    public static LocalDateTime convertLocalDateTime(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof LocalDateTime) {
            return (LocalDateTime) obj;
        } else if (obj instanceof ZonedDateTime) {
            return convertLocalDateTime((ZonedDateTime) obj);
        } else if (obj instanceof LocalDate) {
            return convertLocalDateTime((LocalDate) obj);
        } else if (obj instanceof Date) {
            return convertLocalDateTime((Date) obj);
        } else if (obj instanceof Timestamp) {
            return convertLocalDateTime((Timestamp) obj);
        } else if (obj instanceof String) {
            return convertLocalDateTime((String) obj);
        } else if (obj instanceof Long) {
            return convertLocalDateTime((Long) obj);
        }
        return null;
    }

    public static LocalDateTime convertLocalDateTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime convertLocalDateTime(LocalDate localDate) {
        return localDate.atStartOfDay();
    }

    public static LocalDateTime convertLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static LocalDateTime convertLocalDateTime(Timestamp timestamp) {
        return timestamp.toLocalDateTime();
    }

    public static LocalDateTime convertLocalDateTime(String strDate) {
        return convertStringToLocalDateTime(strDate);
    }

    public static LocalDateTime convertLocalDateTime(Long time) {
        return convertLocalDateTime(new Timestamp(time));
    }

    public static LocalDate convertLocalDate(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof LocalDate) {
            return (LocalDate) obj;
        } else if (obj instanceof ZonedDateTime) {
            return convertLocalDate((ZonedDateTime) obj);
        } else if (obj instanceof LocalDateTime) {
            return convertLocalDate((LocalDateTime) obj);
        } else if (obj instanceof Date) {
            return convertLocalDate((Date) obj);
        } else if (obj instanceof Timestamp) {
            return convertLocalDate((Timestamp) obj);
        } else if (obj instanceof String) {
            return convertLocalDate((String) obj);
        } else if (obj instanceof Long) {
            return convertLocalDate((Long) obj);
        }
        return null;
    }

    public static LocalDate convertLocalDate(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate convertLocalDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }

    public static LocalDate convertLocalDate(Date date) {
        return LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static LocalDate convertLocalDate(Timestamp timestamp) {
        return timestamp.toLocalDateTime().toLocalDate();
    }

    public static LocalDate convertLocalDate(String strDate) {
        return convertStringToLocalDate(strDate);
    }

    public static LocalDate convertLocalDate(Long time) {
        return convertLocalDate(new Timestamp(time));
    }

    public static ZonedDateTime convertZonedDateTime(Object obj) {
        if (obj instanceof ZonedDateTime) {
            return (ZonedDateTime) obj;
        } else if (obj instanceof LocalDateTime) {
            return convertZonedDateTime((LocalDateTime) obj);
        } else if (obj instanceof LocalDate) {
            return convertZonedDateTime((LocalDate) obj);
        } else if (obj instanceof Date) {
            return convertZonedDateTime((Date) obj);
        } else if (obj instanceof Timestamp) {
            return convertZonedDateTime((Timestamp) obj);
        } else if (obj instanceof String) {
            return convertZonedDateTime((String) obj);
        } else if (obj instanceof Long) {
            return convertZonedDateTime((Long) obj);
        }
        return null;
    }

    public static ZonedDateTime convertZonedDateTime(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault());
    }

    public static ZonedDateTime convertZonedDateTime(Date date) {
        return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static ZonedDateTime convertZonedDateTime(Timestamp timestamp) {
        return timestamp.toLocalDateTime().atZone(ZoneId.systemDefault());
    }

    public static ZonedDateTime convertZonedDateTime(String strDate) {
        return convertStringToZonedDateTime(strDate);
    }

    public static ZonedDateTime convertZonedDateTime(Long time) {
        return convertZonedDateTime(new Timestamp(time));
    }

    private static ZonedDateTime convertStringToZonedDateTime(String strDate) {
        switch (DATE_TIME_FORMAT.getFormatter(strDate)) {
            case ZONED_DATE_TIME:
                return ZonedDateTime.parse(strDate, zonedDateFormatter).withZoneSameInstant(ZoneId.systemDefault());
            case LOCAL_DATE_TIME:
                return LocalDateTime.parse(strDate, localDateTimeFormatter).atZone(ZoneId.systemDefault());
            case LOCAL_DATE:
                return LocalDate.parse(strDate, localDateFormatter).atStartOfDay(ZoneId.systemDefault());
            default:
                return ZonedDateTime.parse(strDate, zonedDateFormatter).withZoneSameInstant(ZoneId.systemDefault());
        }
    }

    private static LocalDateTime convertStringToLocalDateTime(String strDate) {
        switch (DATE_TIME_FORMAT.getFormatter(strDate)) {
            case ZONED_DATE_TIME:
                return ZonedDateTime.parse(strDate, zonedDateFormatter).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
            case LOCAL_DATE_TIME:
                return LocalDateTime.parse(strDate, localDateTimeFormatter);
            case LOCAL_DATE:
                return LocalDate.parse(strDate, localDateFormatter).atStartOfDay();
            case LOCAL_DATE_TIME_STRING:
                return LocalDateTime.parse(strDate, simpleStringDateFormatter);
            default:
                return LocalDateTime.parse(strDate, localDateTimeFormatter);
        }
    }

    private static LocalDate convertStringToLocalDate(String strDate) {
        switch (DATE_TIME_FORMAT.getFormatter(strDate)) {
            case ZONED_DATE_TIME:
                return ZonedDateTime.parse(strDate, zonedDateFormatter).withZoneSameInstant(ZoneId.systemDefault()).toLocalDate();
            case LOCAL_DATE_TIME:
                return LocalDateTime.parse(strDate, localDateTimeFormatter).toLocalDate();
            case LOCAL_DATE:
                return LocalDate.parse(strDate, localDateFormatter);
            default:
                return LocalDate.parse(strDate, localDateFormatter);
        }
    }

    public static DateTimeFormatter getStringDateFormatter(String strDate) {
        switch (DATE_TIME_FORMAT.getFormatter(strDate)) {
            case ZONED_DATE_TIME:
                return zonedDateFormatter;
            case LOCAL_DATE_TIME:
                return localDateTimeFormatter;
            case LOCAL_DATE:
                return localDateFormatter;
            case OFFSET_TIME:
                return offsetTimeFormatter;
            case LOCAL_TIME:
                return localTimeFormatter;
            default:
                return defaultDateTimeFormatter;
        }
    }

    public static TemporalAccessor convertStringToDate(String strDate) {
        switch (DATE_TIME_FORMAT.getFormatter(strDate)) {
            case ZONED_DATE_TIME:
                return ZonedDateTime.parse(strDate, zonedDateFormatter).withZoneSameInstant(ZoneId.systemDefault());
            case LOCAL_DATE_TIME:
                return LocalDateTime.parse(strDate, localDateTimeFormatter);
            case LOCAL_DATE:
                return LocalDate.parse(strDate, localDateFormatter);
            case OFFSET_TIME:
                return OffsetTime.parse(strDate, offsetTimeFormatter).withOffsetSameInstant(ZoneOffset.ofHours(9));
            case LOCAL_TIME:
                return LocalTime.parse(strDate, localTimeFormatter);
            default:
                return ZonedDateTime.parse(strDate, DateTimeFormatter.ISO_INSTANT).withZoneSameInstant(ZoneId.systemDefault());
        }
    }

    public static String convertDateToString(TemporalAccessor temporalAccessor) {
        if (temporalAccessor instanceof ZonedDateTime) {
            return convertDateToString((ZonedDateTime) temporalAccessor);
        } else if (temporalAccessor instanceof LocalDateTime) {
            return convertDateToString((LocalDateTime) temporalAccessor);
        } else if (temporalAccessor instanceof LocalDate) {
            return convertDateToString((LocalDate) temporalAccessor);
        } else if (temporalAccessor instanceof OffsetTime) {
            return defaultOffsetTimeFormatter.format(temporalAccessor);
        } else if (temporalAccessor instanceof LocalTime) {
            return DateTimeFormatter.ISO_LOCAL_TIME.format(temporalAccessor);
        } else {
            return DateTimeFormatter.ISO_INSTANT.format(temporalAccessor);
        }
    }

    public static String formatStringDate(String strDate) {
        switch (DATE_TIME_FORMAT.getFormatter(strDate)) {
            case ZONED_DATE_TIME:
                return convertDateToString(ZonedDateTime.parse(strDate, zonedDateFormatter).withZoneSameInstant(ZoneId.systemDefault()));
            case LOCAL_DATE_TIME:
                return convertDateToString(LocalDateTime.parse(strDate, localDateTimeFormatter));
            case LOCAL_DATE:
                return convertDateToString(LocalDate.parse(strDate, localDateFormatter));
            case OFFSET_TIME:
                return convertDateToString(OffsetTime.parse(strDate, offsetTimeFormatter).withOffsetSameInstant(ZoneOffset.ofHours(9)));
            case LOCAL_TIME:
                return convertDateToString(LocalTime.parse(strDate, localTimeFormatter));
            default:
                return convertDateToString(ZonedDateTime.parse(strDate, DateTimeFormatter.ISO_INSTANT).withZoneSameInstant(ZoneId.systemDefault()));
        }
    }

    public static String convertDateToString(LocalDate localDate) {
        return DateTimeFormatter.ISO_DATE.format(localDate);
    }

    public static String convertDateToString(LocalDateTime localDateTime) {
        return defaultDateTimeFormatter.format(localDateTime.atZone(ZoneId.systemDefault()));
    }

    public static String convertDateToString(ZonedDateTime zonedDateTime) {
        return defaultDateTimeFormatter.format(zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()));
    }

    private static DateTimeFormatter defaultDateTimeFormatter() {
        return DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT);
    }

    private static DateTimeFormatter defaultOffsetTimeFormatter() {
        return DateTimeFormatter.ofPattern(DEFAULT_OFFSETTIME_FORMAT);
    }

    private static DateTimeFormatter defaultDateFormatter() {
        return new DateTimeFormatterBuilder()
                .optionalStart()
                .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                .optionalStart().appendLiteral('-').optionalEnd()
                .optionalStart().appendLiteral('/').optionalEnd()
                .appendValue(MONTH_OF_YEAR, 2)
                .optionalStart().appendLiteral('-').optionalEnd()
                .optionalStart().appendLiteral('/').optionalEnd()
                .appendValue(DAY_OF_MONTH, 2)
                .optionalEnd()
                .optionalStart()
                .appendOptional(DateTimeFormatter.ofPattern("yyyyMMdd"))
                .optionalEnd()
                .toFormatter();
    }

    private static DateTimeFormatter defaultTimeFormatter() {
        return new DateTimeFormatterBuilder()
                .optionalStart()
                .optionalStart().appendLiteral('T').optionalEnd()
                .optionalStart().appendLiteral(' ').optionalEnd()
                .appendValue(HOUR_OF_DAY, 2)
                .optionalStart()
                .appendLiteral(':')
                .appendValue(MINUTE_OF_HOUR, 2)
                .optionalStart()
                .appendLiteral(':')
                .appendValue(SECOND_OF_MINUTE, 2)
                .optionalStart()
                .appendFraction(NANO_OF_SECOND, 0, 3, true)
                .optionalEnd()
                .toFormatter();
    }

    private static DateTimeFormatter simpleStringDateFormatter() {
        return new DateTimeFormatterBuilder()
                .optionalStart()
                .appendPattern("yyyyMMddHHmmss")
                .appendValue(ChronoField.MILLI_OF_SECOND, 3)
                .optionalEnd()
                .optionalStart()
                .appendPattern("yyyyMMddHHmmss")
                .appendValue(ChronoField.MILLI_OF_SECOND, 2)
                .optionalEnd()
                .optionalStart()
                .appendPattern("yyyyMMddHH")
                .optionalEnd()
                .optionalStart()
                .appendPattern("yyyyMMddHHmm")
                .optionalEnd()
                .optionalStart()
                .appendPattern("yyyyMMddHHmmss")
                .optionalEnd()
                .toFormatter();
    }

    private static DateTimeFormatter getZonedDateTimeFormatter() {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(defaultDateFormatter())
                .append(defaultTimeFormatter())
                .optionalStart().appendOffsetId().optionalEnd()
                .optionalStart().appendOptional(DateTimeFormatter.ofPattern("Z")).optionalEnd()
                .toFormatter();
    }

    private static DateTimeFormatter getLocalDateTimeFormatter() {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(defaultDateFormatter())
                .append(defaultTimeFormatter())
                .optionalStart().appendOffsetId().optionalEnd()
                .optionalStart().appendOptional(DateTimeFormatter.ofPattern("Z")).optionalEnd()
                .toFormatter();
    }

    private static DateTimeFormatter getLocalDateFormatter() {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(defaultDateFormatter())
                .optionalStart().appendOffsetId().optionalEnd()
                .optionalStart().appendOptional(DateTimeFormatter.ofPattern("Z")).optionalEnd()
                .toFormatter();
    }

    private static DateTimeFormatter getOffsetTimeFormatter() {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(defaultTimeFormatter())
                .optionalStart().appendZoneOrOffsetId().optionalEnd()
                .optionalStart().appendOptional(DateTimeFormatter.ofPattern("Z")).optionalEnd()
                .toFormatter();
    }

    private static DateTimeFormatter getLocalTimeFormatter() {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(defaultTimeFormatter())
                .toFormatter();
    }
    
    @Getter
    public enum DATE_TIME_FORMAT {
        ZONED_DATE_TIME("^"+DATE_REG+TIME_REG+OFFSET_REG+"$"),
        LOCAL_DATE_TIME("^"+DATE_REG+TIME_REG+"$"),
        LOCAL_DATE_TIME_STRING("^"+STR_DATE_TIME_REG+"$"),
        LOCAL_DATE("^"+DATE_REG+OFFSET_REG+"?$"),
        OFFSET_TIME("^"+TIME_REG+OFFSET_REG+"$"),
        LOCAL_TIME("^"+TIME_REG+"$"),
        DEFAULT("");

        private String regExp;


        private DATE_TIME_FORMAT(String regExp) {
            this.regExp = regExp;
        }
        
        public static DATE_TIME_FORMAT getFormatter(String strDate) {
            for (DATE_TIME_FORMAT format : DATE_TIME_FORMAT.values()) {
                if (StringUtil.hasFindByRegex(format.getRegExp(), strDate)) {
                    return format;
                }
            }
            return DEFAULT;
        }

    }
}
