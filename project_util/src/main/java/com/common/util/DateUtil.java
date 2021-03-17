package com.common.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import lombok.Getter;

public class DateUtil {
    public static final String YY                  = "yy";
    public static final String YYMM                = "yyMM";
    public static final String YYMMDD              = "yyMMdd";
    public static final String YYYY                = "yyyy";
    public static final String YYYYMM              = "yyyyMM";
    public static final String YYYYMMDD            = "yyyyMMdd";
    public static final String YYYYMMDDHH          = "yyyyMMddHH";
    public static final String YYYYMMDDHHMM        = "yyyyMMddHHmm";
    public static final String YYYYMMDDHHMMSS      = "yyyyMMddHHmmss";
    public static final String YYYYMMDDHHMMSSSSS   = "yyyyMMddHHmmssSSS";
    public static final String YYYY_MM_DD          = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HHMM     = "yyyy-MM-dd HHmm";
    public static final String YYYY_MM_DD_HH_MM    = "yyyy-MM-dd HH:mm";
    public static final String YYYYMMDD_HHMMSS     = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYYMMDD_HHMMSS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String HHMMSS              = "HHmmss";
    public static final String TIMESTAMP           = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String TIMESTAMP2          = "yyyy-MM-dd HH:mm:ss.SSSZ";
    public static final String TIMESTAMP_SSS_HHMM  = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String TIMESTAMP_SSS_HH    = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    public static final String TIMESTAMP_HHMM      = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public static final String TIMESTAMP_HH        = "yyyy-MM-dd'T'HH:mm:ssX";
    public static final String DATE_EN             = "d MMM, YYYY";

    /**
     * Date 타입을 원하는 format의 문자열로 변환
     * 
     * @param date
     * @param format
     * @return
     */
    public static String dateToString(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat dtFormatter = new SimpleDateFormat(format);
        return dtFormatter.format(date);
    }

    public static String dateToString(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat dtFormatter = new SimpleDateFormat(TIMESTAMP);
        return dtFormatter.format(date);
    }

    public static String dateToString(Date date, SimpleDateFormat formatter) {
        if (date == null) {
            return "";
        }
        return formatter.format(date);
    }

    public static String dateToString(Object obj, SimpleDateFormat formatter) {
        return dateToString(convertDate(obj), formatter);
    }

    /**
     * 날짜 형태의 문자열을 원하는 날짜 포맷의 문자열로 변환
     * 
     * @param strDate
     * @param originalFormat
     * @param resultFormat
     * @return
     */
    public static String getString(String strDate, String originalFormat, String resultFormat) throws ParseException {
        if (StringUtil.isEmpty(strDate)) {
            return "";
        }
        return dateToString(stringToDate(strDate, originalFormat), resultFormat);
    }

    /**
     * 문자열 날짜를 Date 타입으로 변환
     * 
     * @param strDate
     * @param format
     * @return
     */
    public static Date stringToDate(String strDate, String format) throws ParseException {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            formatter.setLenient(false);
            return formatter.parse(strDate.trim());
        } catch (ParseException pex) {
            throw new ParseException("No matching date format for " + strDate, 0);
        }
    }

    public static Date convertFlexibleDate(String strDate, String[] formats) throws ParseException {
        if (StringUtil.isEmpty(strDate))
            return null;
        for (int i = 0; i < formats.length; i++) {
            try {
                SimpleDateFormat dtFormatter = new SimpleDateFormat(formats[i]);
                dtFormatter.setLenient(false);
                return dtFormatter.parse(strDate.trim());
            } catch (ParseException e) {
            }
        }
        throw new ParseException("No matching date format for " + strDate, 0);
    }

    public static Date convertFlexibleDate(String strDate) throws ParseException {
        if (StringUtil.isEmpty(strDate))
            throw new ParseException("Cannot convert empty string to Date.", 0);
        String[] formats = { "MM/dd/yyyy", "MM-dd-yyyy", "yyyy-MM-dd", "MMM dd yyyy", "MMM dd, yyyy", "MMM yyyy", YYYYMMDD, YYYYMMDDHH, YYYYMMDDHHMM, YYYYMMDDHHMMSS, YYYYMMDDHHMMSSSSS, YYYYMMDD_HHMMSS, YYYY_MM_DD_HHMM, YYYYMMDD_HHMMSS_SSS, YYYYMMDD_HHMMSS };
        return convertFlexibleDate(strDate, formats);
    }

    /**
     * 오늘 날짜의 Date 타입을 반환
     * 
     * @return
     */
    public static Date getToday() {
        return new Date();
    }

    /**
     * 오늘 날짜를 원하는 문자열 포맷으로 반환
     * 
     * @return
     */
    public static String getToday(String format) {
        return dateToString(new Date(), format);
    }

    /**
     * 오늘 날짜를 원하는 YYYYMMDD 포맷으로 반환
     * 
     * @return
     */
    public static String getTodayTimestamp() {
        return dateToString(new Date(), TIMESTAMP);
    }
    
    /**
     * 오늘 날짜를 원하는 YYYYMMDD 포맷으로 반환
     * 
     * @return
     */
    public static String getTodayYYYYMMDD() {
        return dateToString(new Date(), YYYYMMDD);
    }

    /**
     * 오늘 날짜를 원하는 YYYYMM 포맷으로 반환
     * 
     * @return
     */
    public static String getTodayYYYYMM() {
        return dateToString(new Date(), YYYYMM);
    }

    /**
     * 오늘 날짜를 원하는 HHMMSS 포맷으로 반환
     * 
     * @return
     */
    public static String getTodayHHMMSS() {
        return dateToString(new Date(), HHMMSS);
    }

    /**
     * 오늘 날짜를 원하는 YYYYMMDDHH 포맷으로 반환
     * 
     * @return
     */
    public static String getTodayYYYYMMDDHH() {
        return dateToString(new Date(), YYYYMMDDHH);
    }

    /**
     * 오늘 날짜를 원하는 YYYYMMDD_HHMMSS_SSS 포맷으로 반환
     * 
     * @return
     */
    public static String getTodayYYYYMMDD_HHMMSS_SSS() {
        return dateToString(new Date(), YYYYMMDD_HHMMSS_SSS);
    }

    /**
     * 오늘 날짜를 원하는 YYYYMMDDHHMMSSSSS 포맷으로 반환
     * 
     * @return
     */
    public static String getTodayYYYYMMDDHHMMSSSSS() {
        return dateToString(new Date(), YYYYMMDDHHMMSSSSS);
    }

    public static String convertShortDate(Date obj) {
        return dateToString(obj, YYYYMMDD);
    }

    public static Date convertShortDate(String str) throws ParseException {
        return stringToDate(str, YYYYMMDD);
    }

    public static Date convertShortDate(String str, Date defaultDate) {
        try {
            return stringToDate(str, YYYYMMDD);
        } catch (ParseException pex) {
            return defaultDate;
        }
    }

    public static boolean compareNullableDates(Date date1, Date date2) {
        if ((date1 == null) && (date2 == null))
            return true;
        if (date1 != null) {
            if (date1.equals(date2))
                return true;
            else
                return false;
        }
        return false;
    }

    public static String getYYYYMMDD(Calendar cal) {
        Locale currentLocale = new Locale("KOREAN", "KOREA");
        String pattern = YYYYMMDD;
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
        return formatter.format(cal.getTime());
    }

    public static String getCurrentDateTime() {
        Date today = new Date();
        Locale currentLocale = new Locale("KOREAN", "KOREA");
        String pattern = YYYYMMDDHHMMSS;
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
        return formatter.format(today);
    }

    public static String getCurrentTime() {
        Date today = new Date();
        Locale currentLocale = new Locale("KOREAN", "KOREA");
        String pattern = HHMMSS;
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, currentLocale);
        return formatter.format(today);
    }

    /**
     * 8자리형태의 년월일
     * 
     * @return
     */
    public static String getCurrentYyyymmdd() {
        return getCurrentDateTime().substring(0, 8);
    }

    /**
     * 6자리 형태의 년월
     * 
     * @return
     */
    public static String getCurrentYyyymm() {
        return getCurrentDateTime().substring(0, 6);
    }

    /**
     * 4자리형태의 년도
     * 
     * @return
     */
    public static String getCurrentYyyy() {
        return getCurrentDateTime().substring(0, 4);
    }

    /**
     * 2자리형태의 년도
     * 
     * @return
     */
    public static String getCurrentYy() {
        return getCurrentDateTime().substring(0, 2);
    }

    /**
     * 현재 년도를 숫자 반환
     * 
     * @return
     */
    public static int getCurrentYear() {
        return Integer.parseInt(getCurrentYyyy());
    }

    /**
     * 2자리 형태의 현재 월
     * 
     * @return
     */
    public static String getCurrentMm() {
        return getCurrentDateTime().substring(4, 6);
    }

    /**
     * 2자리 형태의 현재 월
     * 
     * @return
     */
    public static String getCurrentDd() {
        return getCurrentDateTime().substring(6, 8);
    }

    /**
     * 현재월을 숫자로 반환
     * 
     * @return
     */
    public static int getCurrentMonth() {
        return Integer.parseInt(getCurrentMm());
    }

    /**
     * String Date Format : yyyymmdd
     * 
     * return X means startDate < endDate And difference Days : X Days
     * return 0 means startDate == endDate And difference Days : 0 Days
     * return -X means startDate > endDate And difference Days : X Days
     * 
     * @param startDate
     * @param endDate
     * @return
     */
    public static long getDifferDays(String startDate, String endDate) {
        GregorianCalendar StartDate = getGregorianCalendar(startDate);
        GregorianCalendar EndDate = getGregorianCalendar(endDate);
        long difer = (EndDate.getTime().getTime() - StartDate.getTime().getTime()) / 86400000;
        return difer;
    }

    public static GregorianCalendar getGregorianCalendar(String date) {
        if (date.length() < 14) {
            date = StringUtil.rightPad(date, 14, '0');
        }
        int yyyy = Integer.parseInt(date.substring(0, 4));
        int mm = Integer.parseInt(date.substring(4, 6));
        int dd = Integer.parseInt(date.substring(6, 8));
        int hour = Integer.parseInt(date.substring(8, 10));
        int min = Integer.parseInt(date.substring(10, 12));
        int sec = Integer.parseInt(date.substring(12, 14));
        GregorianCalendar calendar = new GregorianCalendar(yyyy, mm - 1, dd, hour, min, sec);
        return calendar;
    }

    /**
     * 
     * return X means startDate < endDate And difference Days : X Days
     * return 0 means startDate == endDate And difference Days : 0 Days
     * return -X means startDate > endDate And difference Days : X Days
     * 
     * @param startDate
     * @param endDate
     * @return
     */
    public static long getDifferDays(Date startDate, Date endDate) {
        return getDifferDays(dateToString(startDate, YYYYMMDDHHMMSS), dateToString(endDate, YYYYMMDDHHMMSS));
    }

    /**
     * 해당 일자에 특정 시간값을 세팅한다.
     * 
     * @param date
     * @param hour
     * @param min
     * @param sec
     * @return
     */
    public static Date setTime(Date date, int hour, int min, int sec) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, sec);
        return cal.getTime();
    }

    /**
     * 시,분,초를 모두 최소치로 초기화한다. (Date/Timestamp)
     */
    public static Date minimized(Date date) {
        return setTime(date, 0, 0, 0);
    }

    public static Date minimized(Timestamp timestamp) {
        return minimized(new Date(timestamp.getTime()));
    }

    public static Date minimized(LocalDateTime localDateTime) {
        return minimized(convertDate(localDateTime));
    }

    public static Date minimized(LocalDate localDate) {
        return minimized(convertDate(localDate));
    }

    public static Date minimized(String strDate) {
        return minimized(convertDate(strDate));
    }

    public static Date minimized(Long time) {
        return minimized(new Timestamp(time));
    }

    public static Date minimized(Object obj) {
        if (obj instanceof Date) {
            return minimized((Date) obj);
        } else if (obj instanceof Timestamp) {
            return minimized((Timestamp) obj);
        } else if (obj instanceof String) {
            return minimized((String) obj);
        } else if (obj instanceof Long) {
            return minimized((Long) obj);
        } else if (obj instanceof LocalDate) {
            return minimized((LocalDate) obj);
        } else if (obj instanceof LocalDateTime) {
            return minimized((LocalDateTime) obj);
        }
        return minimized(obj.toString());
    }

    public static Timestamp minimizedToTimestamp(Object obj) {
        return convertTimestamp(minimized(obj));
    }

    /**
     * 시,분,초를 모두 최대치로 초기화한다. (Date/Timestamp)
     */
    public static Date maximized(Date date) {
        return setTime(date, 23, 59, 59);
    }

    public static Date maximized(Timestamp timestamp) {
        return maximized(new Date(timestamp.getTime()));
    }

    public static Date maximized(String strDate) {
        return maximized(convertDate(strDate));
    }

    public static Date maximized(Long time) {
        return maximized(new Timestamp(time));
    }

    public static Date maximized(LocalDateTime localDateTime) {
        return maximized(convertDate(localDateTime));
    }

    public static Date maximized(LocalDate localDate) {
        return maximized(convertDate(localDate));
    }

    public static Date maximized(Object obj) {
        if (obj instanceof Date) {
            return maximized((Date) obj);
        } else if (obj instanceof Timestamp) {
            return maximized((Timestamp) obj);
        } else if (obj instanceof String) {
            return maximized((String) obj);
        } else if (obj instanceof Long) {
            return maximized((Long) obj);
        } else if (obj instanceof LocalDate) {
            return maximized((LocalDate) obj);
        } else if (obj instanceof LocalDateTime) {
            return maximized((LocalDateTime) obj);
        }
        return maximized(obj.toString());
    }

    public static Timestamp maximizedToTimestamp(Object obj) {
        return convertTimestamp(maximized(obj));
    }

    /**
     * 시간 타입 변경
     */
    public static Timestamp convertTimestamp(Object obj) {
        if (obj instanceof Date) {
            return convertTimestamp((Date) obj);
        } else if (obj instanceof Timestamp) {
            return convertTimestamp((Timestamp) obj);
        } else if (obj instanceof String) {
            return convertTimestamp((String) obj);
        } else if (obj instanceof LocalDate) {
            return convertTimestamp((LocalDate) obj);
        } else if (obj instanceof LocalDateTime) {
            return convertTimestamp((LocalDateTime) obj);
        }
        return convertTimestamp(obj.toString());
    }

    public static Timestamp convertTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    public static Timestamp convertTimestamp(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime);
    }

    public static Timestamp convertTimestamp(LocalDate localDate) {
        return convertTimestamp(localDate.atStartOfDay());
    }

    public static Timestamp convertTimestamp(String strDate) {
        return convertTimestamp(convertDate(strDate));
    }

    public static Timestamp convertTimestamp(Long time) {
        return new Timestamp(time);
    }

    public static String getStringToFormat(String date, String format) {
        if (StringUtil.isEmpty(date)) {
            return "";
        }
        for (DATE_FORMAT dateFormat : DATE_FORMAT.values()) {
            if (StringUtil.hasFindByRegex(dateFormat.getRegExp(), date)) {
                try {
                    return DateUtil.getString(date, dateFormat.getFormat(), format);
                } catch (Exception e) {
                }
            }
        }
        return StringUtil.numberValue(date);
    }
    
    public static Date addYears(Date date, int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    public static Date addMonths(Date date, int amount) {
        return add(date, Calendar.MONTH, amount);
    }
    
    public static Date addDays(Date date, int amount) {
        return add(date, Calendar.DAY_OF_MONTH, amount);
    }
    
    private static Date add(Date date, int calendarField, int amount) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }
    
    public static String getStringCurrentAddDays(int days) {
        return getStringAddDays(new Date(), days);
    }

    public static String getStringCurrentAddDays(int days, String format) {
        return getStringAddDays(new Date(), days, format);
    }

    public static String getStringAddDays(Date date, int days) {
        return convertShortDate(addDays(date, days));
    }

    public static String getStringAddDays(Date date, int days, String format) {
        return dateToString(addDays(date, days), format);
    }

    public static String getStringAddDays(String strDate, int days) {
        return convertShortDate(addDays(convertDate(strDate), days));
    }

    public static String getStringAddDays(String strDate, int days, String format) {
        return dateToString(addDays(convertDate(strDate), days), format);
    }
    

    /**
     * 현재 시간을 기준으로 시/분/초를 모두 초단위로 변경하여
     * 입력받은 값(주기값)에 일치하는지 여부
     * 
     * @param periodSeconds
     * @return
     */
    public static boolean isMomentByPeriod(int periodSeconds) {
        String todayHHMMSS = getTodayHHMMSS();
        int mm = Integer.parseInt(todayHHMMSS.substring(0, 2)) * 60 * 60;
        int dd = Integer.parseInt(todayHHMMSS.substring(2, 4)) * 60;
        int ss = Integer.parseInt(todayHHMMSS.substring(4, 6));
        return ((mm + dd + ss) % periodSeconds) == 0;
    }

    public static String getDateStr(Date date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static Date convertDate(String strDate) {
        if (StringUtil.isEmpty(strDate)) {
            return null;
        }
        for (DATE_FORMAT dateFormat : DATE_FORMAT.values()) {
            if (StringUtil.hasFindByRegex(dateFormat.getRegExp(), strDate)) {
                try {
                    return DateUtil.stringToDate(strDate, dateFormat.getFormat());
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    public static Date convertDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date convertDate(LocalDate localDate) {
        return convertDate(localDate.atStartOfDay());
    }

    public static Date convertDate(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return (Date) obj;
        } else if (obj instanceof String) {
            return convertDate((String) obj);
        } else if (obj instanceof Timestamp) {
            return new Date(((Timestamp) obj).getTime());
        } else if (obj instanceof java.sql.Date) {
            return new Date(((java.sql.Date) obj).getTime());
        } else if (obj instanceof Long) {
            return new Date((Long) obj);
        } else if (obj instanceof LocalDateTime) {
            return convertDate((LocalDateTime) obj);
        } else if (obj instanceof LocalDate) {
            return convertDate((LocalDate) obj);
        }
        return null;
    }

    // 날짜유형
    @Getter
    public enum DATE_FORMAT {
        TIMESTAMP_SSS_HHMM("^([0-9]){4}[-]([0-9]){2}[-]([0-9]){2}[T]([0-9]){2}[:]([0-9]){2}[:]([0-9]){2}[.]([0-9]){3}([+][0-9]{2}[:][0-9]{2}|[Z])$","yyyy-MM-dd'T'HH:mm:ss.SSSXXX","offsetDatetime"), 
        TIMESTAMP_SSS_HHMM2("^([0-9]){4}[-]([0-9]){2}[-]([0-9]){2}[T]([0-9]){2}[:]([0-9]){2}[:]([0-9]){2}[.]([0-9]){3}([+][0-9]{2}[0-9]{2}|[Z])$","yyyy-MM-dd'T'HH:mm:ss.SSSXX","offsetDatetime"), 
        TIMESTAMP_SSS_HH("^([0-9]){4}[-]([0-9]){2}[-]([0-9]){2}[T]([0-9]){2}[:]([0-9]){2}[:]([0-9]){2}[.]([0-9]){3}([+][0-9]{2}|[Z])$","yyyy-MM-dd'T'HH:mm:ss.SSSX","offsetDatetime"), 
        TIMESTAMP_HHMM("^([0-9]){4}[-]([0-9]){2}[-]([0-9]){2}[T]([0-9]){2}[:]([0-9]){2}[:]([0-9]){2}([+][0-9]{2}[:][0-9]{2}|[Z])$","yyyy-MM-dd'T'HH:mm:ssXXX","offsetDatetime"),
        TIMESTAMP_HHMM2("^([0-9]){4}[-]([0-9]){2}[-]([0-9]){2}[T]([0-9]){2}[:]([0-9]){2}[:]([0-9]){2}([+][0-9]{2}[0-9]{2}|[Z])$","yyyy-MM-dd'T'HH:mm:ssXXX","offsetDatetime"), 
        TIMESTAMP_HH("^([0-9]){4}[-]([0-9]){2}[-]([0-9]){2}[T]([0-9]){2}[:]([0-9]){2}[:]([0-9]){2}([+][0-9]{2}|[Z])$","yyyy-MM-dd'T'HH:mm:ssX","offsetDatetime"), 
        TIMESTAMP2_SSS_HHMM("^([0-9]){4}[-]([0-9]){2}[-]([0-9]){2} ([0-9]){2}[:]([0-9]){2}[:]([0-9]){2}[.]([0-9]){3}([+][0-9]{2}[:][0-9]{2}|[Z])$","yyyy-MM-dd HH:mm:ss.SSSXXX","offsetDatetime"), 
        TIMESTAMP2_SSS_HHMM2("^([0-9]){4}[-]([0-9]){2}[-]([0-9]){2} ([0-9]){2}[:]([0-9]){2}[:]([0-9]){2}[.]([0-9]){3}([+][0-9]{2}[0-9]{2}|[Z])$","yyyy-MM-dd HH:mm:ss.SSSXX","offsetDatetime"), 
        TIMESTAMP2_SSS_HH("^([0-9]){4}[-]([0-9]){2}[-]([0-9]){2} ([0-9]){2}[:]([0-9]){2}[:]([0-9]){2}[.]([0-9]){3}([+][0-9]{2}|[Z])$","yyyy-MM-dd HH:mm:ss.SSSX","offsetDatetime"), 
        TIMESTAMP2_HHMM("^([0-9]){4}[-]([0-9]){2}[-]([0-9]){2} ([0-9]){2}[:]([0-9]){2}[:]([0-9]){2}([+][0-9]{2}[:][0-9]{2}|[Z])$","yyyy-MM-dd HH:mm:ssXXX","offsetDatetime"), 
        TIMESTAMP2_HHMM2("^([0-9]){4}[-]([0-9]){2}[-]([0-9]){2} ([0-9]){2}[:]([0-9]){2}[:]([0-9]){2}([+][0-9]{2}[0-9]{2}|[Z])$","yyyy-MM-dd HH:mm:ssXXX","offsetDatetime"), 
        TIMESTAMP2_HH("^([0-9]){4}[-]([0-9]){2}[-]([0-9]){2} ([0-9]){2}[:]([0-9]){2}[:]([0-9]){2}([+][0-9]{2}|[Z])$","yyyy-MM-dd HH:mm:ssX","offsetDatetime"),
        YYYYMMDD("^([0-9]){8}$","yyyyMMdd","date"), 
        YYYYMMDDHH("^([0-9]){10}$","yyyyMMddHH","datetime"), 
        YYYYMMDDHHMM("^([0-9]){12}$","yyyyMMddHHmm","datetime"),
        YYYYMMDDHHMMSS("^([0-9]){14}$","yyyyMMddHHmmss","datetime"),
        YYYYMMDDHHMMSSSSS("^([0-9]){17}$","yyyyMMddHHmmssSSS","datetime"), 
        YYYY_MM_DD("^([0-9]){4}[-]([0-9]){2}[-]([0-9]){2}$","yyyy-MM-dd","date"), 
        YYYY_MM_DD2("^([0-9]){4}[/]([0-9]){2}[/]([0-9]){2}$","yyyy/MM/dd","date"), 
        YYYY_MM_DD_HHMM("^([0-9]){4}[-]([0-9]){2}[-]([0-9]){2} ([0-9]){4}$","yyyy-MM-dd HHmm","datetime"), 
        YYYY_MM_DD_HH_MM("^([0-9]){4}[-]([0-9]){2}[-]([0-9]){2} ([0-9]){2}[:]([0-9]){2}$","yyyy-MM-dd HH:mm","datetime"), 
        YYYYMMDD_HHMMSS("^([0-9]){4}[-]([0-9]){2}[-]([0-9]){2} ([0-9]){2}[:]([0-9]){2}[:]([0-9]){2}$","yyyy-MM-dd HH:mm:ss","datetime"), 
        YYYYMMDD_HHMMSS2("^([0-9]){4}[-]([0-9]){2}[-]([0-9]){2} ([0-9]){2}[：]([0-9]){2}[：]([0-9]){2}$","yyyy-MM-dd HH：mm：ss","datetime"), 
        YYYYMMDD_HHMMSS_SSS("^([0-9]){4}[-]([0-9]){2}[-]([0-9]){2} ([0-9]){2}[:]([0-9]){2}[:]([0-9]){2}[.]([0-9]){3}$","yyyy-MM-dd HH:mm:ss.SSS","datetime");

        private String regExp;
        private String format;
        private String type;

        private DATE_FORMAT(String regExp, String format, String type) {
            this.regExp = regExp;
            this.format = format;
            this.type = type;
        }

        public static DATE_FORMAT getDateFormat(String name) {
            if (StringUtil.isNotEmpty(name)) {
                for (DATE_FORMAT dateFormat : DATE_FORMAT.values()) {
                    if (dateFormat.name().equals(name)) {
                        return dateFormat;
                    }
                }
            }
            return null;
        }

        public static DATE_FORMAT getDateFormatByDate(String strDate) {
            if (StringUtil.isNotEmpty(strDate)) {
                for (DATE_FORMAT dateFormat : DATE_FORMAT.values()) {
                    if (StringUtil.hasFindByRegex(dateFormat.getRegExp(), strDate)) {
                        return dateFormat;
                    }
                }
            }
            return null;
        }

        public static DateFormat getFormater(String strDate) {
            for (DATE_FORMAT format : DATE_FORMAT.values()) {
                if (StringUtil.hasFindByRegex(format.getRegExp(), strDate)) {
                    return new SimpleDateFormat(format.getFormat());
                }
            }
            return null;
        }

        public static String getPattern(String strDate) {
            for (DATE_FORMAT format : DATE_FORMAT.values()) {
                if (StringUtil.hasFindByRegex(format.getRegExp(), strDate)) {
                    return format.getFormat();
                }
            }
            return null;
        }

        public static DateFormat[] getFormaters() {
            List<DateFormat> df = new ArrayList<>();
            for (DATE_FORMAT dateFormat : DATE_FORMAT.values()) {
                df.add(new SimpleDateFormat(dateFormat.getFormat()));
            }
            return df.toArray(new DateFormat[DATE_FORMAT.values().length]);
        }

        public static String[] getFormats() {
            List<String> formats = new ArrayList<>();
            for (DATE_FORMAT dateFormat : DATE_FORMAT.values()) {
                formats.add(dateFormat.getFormat());
            }
            return formats.toArray(new String[formats.size()]);
        }
    }
}
