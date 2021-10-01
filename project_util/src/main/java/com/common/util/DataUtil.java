package com.common.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@SuppressWarnings("unchecked")
public class DataUtil {
    
    private static final String equals = "=";
    private static final String notEquals = "!";
    private static final String like = "?";
    private static final String start = "^";
    private static final String end = "$";
    private static final String goe = "<=";
    private static final String loe = ">=";
    private static final String gt = "<";
    private static final String lt = ">";
    private static final String _ne = "_ne";
    private static final String _eq = "_eq";
    private static final String _like = "_like";
    private static final String _st = "_st";
    private static final String _end = "_end";
    private static final String _goe = "_from";
    private static final String _loe = "_to";
    private static final String _gt = "_gt";
    private static final String _lt = "_lt";
    
    public static Map<String, Object> searchMapPrameterConvert(Map<String, Object> map) {
        Map<String, Object> temp = new HashMap<>();
        String value;
        Set<String> keys = map.keySet();
        for (String key : keys ) {
            if (isValidParam(map.get(key))) {
                try {
                    if (map.get(key) instanceof Arrays || map.get(key) instanceof Collection<?>) {
                        temp.put(key, map.get(key));
                        
                    } else {
                        value = stringConvert(map.get(key));
                        if (isOrSearch(key)) {
                            orParamConvert(temp, key, value);
                        } else {
                            andParamConvert(temp, key, value);
                        }
                    }
                } catch (Exception e) {
                    temp.put(key, map.get(key));
                }
                
            }
        }
        return temp;
    }
    
    private static boolean isOrSearch(String key) {
        if (key == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("^.+(\\|).+$");
        Matcher matcher = pattern.matcher(key);
        return matcher.find();
    }

    private static void andParamConvert(Map<String, Object> temp, String key, String value) throws Exception {
        if (value.startsWith(equals)) {
            temp.put(key+_eq, StringUtil.replaceFirst(value, equals, ""));
        } else if (value.startsWith(notEquals)) {
            temp.put(key+_ne, StringUtil.replaceFirst(value, notEquals, ""));
        } else if (value.startsWith(like)) {
            temp.put(key+_like, StringUtil.replaceFirst(value, like, ""));
        } else if (value.startsWith(start)) {
            temp.put(key+_st, StringUtil.replaceFirst(value, start, ""));
        } else if (value.startsWith(end)) {
            temp.put(key+_end, StringUtil.replaceFirst(value, end, ""));
        } else if (value.startsWith(goe)) {
            temp.put(key+_goe, StringUtil.replaceFirst(value, goe, ""));
        } else if (value.startsWith(loe)) {
            temp.put(key+_loe, StringUtil.replaceFirst(value, loe, ""));
        } else if (value.startsWith(gt)) {
            temp.put(key+_gt, StringUtil.replaceFirst(value, gt, ""));
        } else if (value.startsWith(lt)) {
            temp.put(key+_lt, StringUtil.replaceFirst(value, lt, ""));
        } else {
            temp.put(key, value);
        }
    }
    
    private static void orParamConvert(Map<String, Object> temp, String key, String value) throws Exception {
        String[] feilds = StringUtil.split(key, "|");
        List<String> params = new ArrayList<>();
        if (value.startsWith(equals)) {
            value = StringUtil.replaceFirst(value, equals, "");
            for (String feild : feilds) {
                params.add(feild.concat(key+_eq));
            }
        } else if (value.startsWith(notEquals)) {
            value = StringUtil.replaceFirst(value, notEquals, "");
            for (String feild : feilds) {
                params.add(feild.concat(key+_ne));
            }
        } else if (value.startsWith(like)) {
            value = StringUtil.replaceFirst(value, like, "");
            for (String feild : feilds) {
                params.add(feild.concat(key+_like));
            }
        } else if (value.startsWith(start)) {
            value = StringUtil.replaceFirst(value, start, "");
            for (String feild : feilds) {
                params.add(feild.concat(key+_st));
            }
        } else if (value.startsWith(goe)) {
            value = StringUtil.replaceFirst(value, goe, "");
            for (String feild : feilds) {
                params.add(feild.concat(key+_goe));
            }
        } else if (value.startsWith(loe)) {
            value = StringUtil.replaceFirst(value, loe, "");
            for (String feild : feilds) {
                params.add(feild.concat(key+_loe));
            }
        } else if (value.startsWith(gt)) {
            value = StringUtil.replaceFirst(value, gt, "");
            for (String feild : feilds) {
                params.add(feild.concat(key+_gt));
            }
        } else if (value.startsWith(lt)) {
            value = StringUtil.replaceFirst(value, lt, "");
            for (String feild : feilds) {
                params.add(feild.concat(key+_lt));
            }
        } else {
            for (String feild : feilds) {
                params.add(feild.concat(key));
            }
        }
        temp.put(StringUtil.join("|", params), value);
    }
    
    public static Map<String, Object> updateMapPrameterConvert(Map<String, Object> map) {
        map.remove("isDeleted");
        Map<String, Object> temp = new HashMap<>();
        Set<String> keys = map.keySet();
        for (String key : keys ) {
            if (StringUtil.startsWith(key, "is")) {
                if (map.get(key) instanceof Boolean) {
                    temp.put(key, (Boolean) map.get(key));
                } else {
                    if (StringUtil.in((String) map.get(key), "true", "1")) {
                        temp.put(key, true);
                    } else if (StringUtil.in((String) map.get(key), "false", "0")) {
                        temp.put(key, false);
                    } else {
                        temp.put(key, map.get(key));
                    }
                }
            } else if (StringUtil.contains(key, "Date") || StringUtil.contains(key, "date")){
                if (map.get(key) instanceof String) {
                    if (StringUtil.equals((String) map.get(key), "Invalid date")) {
                        temp.put(key, null);
                    } else {
                        temp.put(key, map.get(key));
                    }
                } else {
                    temp.put(key, map.get(key));
                }
            } else {
                temp.put(key, map.get(key));
            }
        }
        return temp;
    }
    
    public static boolean isTrue(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof Boolean) {
            return (Boolean) obj;
        } else if (obj instanceof String) {
            if (StringUtil.in((String) obj, "true", "1")) {
                return true;
            }
            return false;
        } else if (obj instanceof Integer) {
            if ((Integer) obj == 1) {
                return true;
            }
            return false;
        }
        return false;
    }
    
    
    public static String stringConvert(Object obj) {
        if(obj == null) {
            return null;
        }
        if(obj instanceof String) {
            return (String) obj;
        } else {
            return obj.toString();
        }
    }
    
    public static Integer integerConvert(Object obj) {
        if(obj == null) {
            return null;
        }
        if(obj instanceof Integer) {
            return (Integer) obj;
        } else {
            String target = stringConvert(obj);
            if (StringUtil.hasFindByRegex(RegularExpression.INTEGER, target)) {
                return Integer.valueOf(target);
            }
        }
        return -1;
    }
    
    public static Double doubleConvert(Object obj) {
        if(obj == null) {
            return null;
        }
        if(obj instanceof Double) {
            return (Double) obj;
        } else {
            String target = stringConvert(obj);
            if (StringUtil.hasFindByRegex(RegularExpression.NUMERIC, target)) {
                return Double.valueOf(target);
            }
        }
        return -1.1d;
    }
    
    public static Long longConvert(Object obj) {
        if(obj == null) {
            return null;
        }
        if(obj instanceof Long) {
            return (Long) obj;
        } else {
            String target = stringConvert(obj);
            if (StringUtil.hasFindByRegex(RegularExpression.INTEGER, target)) {
                return Long.valueOf(target);
            }
        }
        return -1l;
    }
    
    public static BigInteger bigintegerConvert(Object obj) {
        if(obj == null) {
            return null;
        }
        if(obj instanceof BigInteger) {
            return (BigInteger) obj;
        } else {
            if (StringUtil.hasFindByRegex(RegularExpression.INTEGER, stringConvert(obj))) {
                Long target = longConvert(obj);
                return BigInteger.valueOf(target);
            }
        }
        return BigInteger.valueOf(-1l);
    }
    
    public static BigDecimal bigDecimalConvert(Object obj) {
        if(obj == null) {
            return null;
        }
        if(obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        } else {
            if (StringUtil.hasFindByRegex(RegularExpression.NUMERIC, stringConvert(obj))) {
                Double target = doubleConvert(obj);
                return BigDecimal.valueOf(target);
            }
        }
        return BigDecimal.valueOf(-1d);
    }
    
    public static <T> List<T> listConvert(Object obj) {
        if(obj == null) {
            return null;
        }
        List<T> list = new ArrayList<>();
        if (obj.getClass().isArray()) {
            list = Arrays.asList((T[])obj);
        } else if (obj instanceof Arrays) {
            list = Arrays.asList((T[])obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<T>)obj);
        } else if (obj instanceof String) {
            list = (List<T>) listConvert((String) obj);
        }
        return list;
    }
    
    public static <T> List<T> listConvert(Object obj, Class<T> classType) {
        if(obj == null) {
            return null;
        }
        List<T> list = new ArrayList<>();
        if (obj.getClass().isArray()) {
            list = new ArrayList<>(Arrays.asList((T[])obj));
        } else if (obj instanceof Arrays) {
            list = new ArrayList<>(Arrays.asList((T[])obj));
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<T>)obj);
        } else if (obj instanceof String) {
            list = listConvert((String) obj, classType);
        }
        return list;
    }
    
    public static <T> List<T> listConvert(String str, Class<T> classType) {
        if(str == null) {
            return null;
        }
        String[] splits = str.split(",");
        List<T> list = new ArrayList<>();
        if (String.class.equals(classType)) {
            for (String splitStr : splits) {
                list.add((T) String.valueOf(splitStr));
            }
        } else if (Long.class.equals(classType)) {
            for (String splitStr : splits) {
                list.add((T) Long.valueOf(splitStr));
            }
        } else if (Integer.class.equals(classType)) {
            for (String splitStr : splits) {
                list.add((T) Integer.valueOf(splitStr));
            }
        } else if (Double.class.equals(classType)) {
            for (String splitStr : splits) {
                list.add((T) Double.valueOf(splitStr));
            }
        } else {
            for (String splitStr : splits) {
                list.add((T) String.valueOf(splitStr));
            }
        }
        return list;
    }
    
    
    public static List<String> listConvert(String str) {
        if(str == null) {
            return null;
        }
        List<String> list = new ArrayList<>(Arrays.asList(str.split(",")));
        return list;
    }
    
    /**
     * 소숫점 targetDecimal 자리 밑에서 반올림
     * @param num
     * @param targetDecimal
     * @return
     */
    public static Double round(Double num, Integer targetDecimal) {
        if (num == null || targetDecimal == null) { 
            return num;
        }
        Double target = Math.pow(10, targetDecimal);
        Double result = Math.round(num*target)/target;
        return result;
    }
    public static Double round(Double num) {
        return round(num, 0);
    }
    
    /**
     * 소숫점 targetDecimal 자리 밑에서 올림
     * @param num
     * @param targetDecimal
     * @return
     */
    public static Double ceil(Double num, Integer targetDecimal) {
        if (num == null || targetDecimal == null) { 
            return num;
        }
        Double target = Math.pow(10, targetDecimal);
        Double result = Math.ceil(num*target)/target;
        return result;
    }
    public static Double ceil(Double num) {
        return ceil(num, 0);
    }
    
    /**
     * 소숫점 targetDecimal 자리 밑에서 버림
     * @param num
     * @param targetDecimal
     * @return
     */
    public static Double floor(Double num, Integer targetDecimal) {
        if (num == null || targetDecimal == null) { 
            return num;
        }
        Double target = Math.pow(10, targetDecimal);
        Double result = Math.floor(num*target)/target;
        return result;
    }
    public static Double floor(Double num) {
        return floor(num, 0);
    }
    
    public static Object nvlObj(Object obj, Object rep_val) {
        if (StringUtil.isEmptyObj(obj)) {
            return rep_val;
        }
        return obj;
    }
    
    public static Double nvlDouble(Double val) {
        return isNaN(String.valueOf(val)) ? 0.0d :(Double)nvlObj(val, 0.0d);
    }
    
    public static Long nvlLong(Long val) {
        return isNaN(String.valueOf(val)) ? 0l : (Long)nvlObj(val, 0l);
    }
    
    public static Integer nvlInteger(Integer val) {
        return isNaN(String.valueOf(val)) ? 0 :(Integer)nvlObj(val, 0);
    }
    
    private static boolean isNaN(String val) {
        if (StringUtil.equals(val, "NaN")) {
            return true;
        }
        return false;
    }
    
    public static Map<String, Object> createMap(Object... keyValues) {
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "keyValues의 길이는 짝수 여야 합니다. 현재 배열의 길이는  "
                            + keyValues.length+ " 입니다.");
        }

        Map<String, Object> values = new HashMap<String, Object>();

        for (int x = 0; x < keyValues.length; x+=2) {
          values.put(stringConvert(keyValues[x]), keyValues[x + 1]);
        }

        return values;
    }
    
    public static <T> List<T> createList(T... t) {
        return new ArrayList<T>(Arrays.asList(t));
    }

    public static <T> T[] createArray(T... t) {
        return t;
    }
    
    public static <T> Page<T> createPage(Pageable pageable, T... t) {
        return new PageImpl<T>(createList(t), pageable, t.length);
    }
    
    public static <T> Page<T> createPage(Pageable pageable, List<T> list) {
        return new PageImpl<T>(list, pageable, list.size());
    }
    
    public static <T> Boolean isEmptyList(Collection<T> collections) {
        if (collections == null || collections.isEmpty()) {
            return true;
        }
        return false;
    }
    
    public static Boolean isValidParam(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof Collection<?>) {
            return true;
        } else {
            return StringUtil.isNotEmptyObj(obj);
        }
    }
}
