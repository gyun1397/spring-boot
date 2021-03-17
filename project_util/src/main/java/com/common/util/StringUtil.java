package com.common.util;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("rawtypes")
public class StringUtil {
    public final static String NUM                             = "([0-9])";
    public final static String ENG                             = "([a-zA-Z])";
    public final static String KR                              = "([ㄱ-힣])";
    public final static String NUM_ENG                         = "([0-9a-zA-Z])";
    public final static String NUM_KR                          = "([0-9ㄱ-힣])";
    public final static String ENG_KR                          = "([a-zA-Zㄱ-힣])";
    public final static String NUM_ENG_KR                      = "([0-9a-zA-Zㄱ-힣])";
    public final static String INTEGER                         = "^(-?[0-9]+)$";
    public final static String NUMERIC                         = "^(-?(?:[0-9]*\\.)?[0-9]+)$";
    
    
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    public static String nvl(String str) {
        return nvl(str, "");
    }

    public static String nvl(String str, String replace) {
        if (isEmpty(str)) {
            if (isEmpty(replace)) {
                str = "";
            } else {
                str = replace;
            }
        }
        return str;
    }

    public static String nvlObj(Object obj) {
        return nvlObj(obj, "");
    }

    public static String nvlObj(Object obj, String replace) {
        if (obj == null) {
            return replace;
        } else if (obj instanceof Collection<?>) {
            return (((Collection) obj).isEmpty() ? replace : obj.toString());
        } else if (obj instanceof Map<?,?>) {
            return (((Map) obj).isEmpty() ? replace : obj.toString());
        } else {
            return nvl(obj.toString(), replace);
        }
    }

    
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        if ("".equals(str)) {
            return true;
        }
        return false;
    }

    public static boolean isBlank(String[] strArray) {
        if (strArray == null) {
            return true;
        }
        if (strArray.length > 0) {
            return false;
        }
        return true;
    }

    /**
     * 특정문자를 변환한다.
     *
     * @param str
     * @param pattern
     * @param replace
     * @return String
     */
    public static String replace(String str, String pattern, String replace) {
        if (isEmpty(str) || isEmpty(pattern) || str.indexOf(pattern) == -1) {
            return str;
        }
        replace = isEmpty(replace) ? "" : replace;
        
        final int len = pattern.length();
        StringBuffer sb = new StringBuffer();
        int found = -1;
        int start = 0;
        while ((found = str.indexOf(pattern, start)) != -1) {
            sb.append(str.substring(start, found));
            sb.append(replace);
            start = found + len;
        }
        sb.append(str.substring(start));
        return sb.toString();
    }

    public static String replaceAll(String str, String pattern, String replace) {
        return replace(str, pattern, replace);
    }
    
    public static String replaceFirst(String str, String pattern, String replace) {
        if (isEmpty(str) || isEmpty(pattern) || str.indexOf(pattern) == -1) {
            return str;
        }
        replace = isEmpty(replace) ? "" : replace;
        
        final int len = pattern.length();
        StringBuffer sb = new StringBuffer();
        int found = -1;
        int start = 0;
        if ((found = str.indexOf(pattern, start)) != -1) {
            sb.append(str.substring(start, found));
            sb.append(replace);
            start = found + len;
        }
        sb.append(str.substring(start));
        return sb.toString();
    }
    
    public static String replaceLast(String str, String pattern, String replace) {
        if (isEmpty(str) || isEmpty(pattern) || str.indexOf(pattern) == -1) {
            return str;
        }
        replace = isEmpty(replace) ? "" : replace;
        
        final int len = pattern.length();
        StringBuffer sb = new StringBuffer();
        int found = -1;
        int start = 0;
        if ((found = str.lastIndexOf(pattern)) != -1) {
            sb.append(str.substring(start, found));
            sb.append(replace);
            start = found + len;
        }
        sb.append(str.substring(start));
        return sb.toString();
    }
    
    /**
     * 특정 문자를 제거한다.
     * @param str
     * @param remove
     * @return
     */
    public static String remove(String str, String remove) {
        return replaceFirst(str, remove, "");
    }
    public static String removeAll(String str, String remove) {
        return replaceAll(str, remove, "");
    }

    /**
     * String을 특정 문자를 기준으로 나누어 배열형태로 반환한다.
     *
     * @param str
     * @param delimeter
     * @return String[]
     */
    public static String[] split(String str, String delimeter) {
        if (str == null) {
            return null;
        }
        if (isEmpty(delimeter)) {
            return new String[] {str};
        }
        List<String> list = new ArrayList<>();
        int nCount = 0, nLastIndex = 0;
        int indexValue = 0;
        try {
            nLastIndex = str.indexOf(delimeter);
            if (nLastIndex == -1) {
                list.add(0, str);
            } else {
                indexValue = str.indexOf(delimeter);
                while (indexValue > -1) {
                    nLastIndex = str.indexOf(delimeter);
                    list.add(nCount, str.substring(0, nLastIndex));
                    str = str.substring(nLastIndex + delimeter.length(), str.length());
                    nCount++;
                    indexValue = str.indexOf(delimeter);
                }
                list.add(nCount, str);
            }
        } catch (Exception e) {
            return null;
        }
        return (String[]) (list.toArray(new String[0]));
    }


    /**
     * 왼쪽(Left)에 문자열을 끼어 넣는다. width는 문자열의 전체 길이를 나타내며 pad는 끼어 넣을 char
     *
     * @param str
     * @param width
     * @param pad
     * @return String
     */
    public static String leftPad(String str, int width, char pad) {
        str = isEmpty(str) ? "" : str;
        StringBuffer sb = new StringBuffer();
        for (int i = str.length(); i < width; i++) {
            sb.append(pad);
        }
        sb.append(str);
        String returnValue = sb.toString();
        returnValue = returnValue.substring(0, width);
        return returnValue;
    }

    /**
     * 오른쪽(right)에 문자열을 끼어 넣는다. width는 문자열의 전체 길이를 나타내며, pad는 끼어 넣을 char
     *
     * @param str
     * @param width
     * @param pad
     * @return String
     */
    public static String rightPad(String str, int width, char pad) {
        str = isEmpty(str) ? "" : str;
        if (str.length() >= width) {
            return str.substring(0, width);
        }
        StringBuffer sb = new StringBuffer();
        for (int i = str.length(); i < width; i++) {
            sb.append(pad);
        }
        return str + sb.toString();
    }


    public static boolean contains(String str, String search) {
        if (str == null || search == null) {
            return false;
        }
        return str.indexOf(search) >= 0;
    }
    
    /**
     * 입력받은 문자열 str이 prefix로 시작하는지 확인 한다.
     * 
     * @param str
     * @param prefix
     * @return
     */
    public static boolean startsWith(String str, String prefix) {
        if (str == null || prefix == null) {
            return str == prefix;
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return str.startsWith(prefix);
    }
    
    /**
     * 입력받은 문자열 str이 suffix로 끝나는지 확인 한다.
     * 
     * @param str
     * @param suffix
     * @return
     */
    public static boolean endsWith(String str, String suffix) {
        if (str == null || suffix == null) {
            return str == suffix;
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        return str.endsWith(suffix);
    }

    /**
     * str 값이 pattern으로 구성되었는지 체크한다.
     *
     * @param str
     *            확인하고자 하는 문자열
     * @param pattern
     *            패턴 (예, 숫자 = "0123456789")
     * @return true/false
     */
    public static boolean isPattern(String str, String pattern) {
        if (str == null || pattern == null) {
            return str == pattern;
        }
        if (pattern.length() > str.length()) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (pattern.indexOf(str.charAt(i)) < 0) {
                return false;
            }
        }
        return true;
    }


    public static boolean isEmptyObj(Object obj) {
        return isEmpty(nvlObj(obj, ""));
    }

    public static boolean isNotEmptyObj(Object obj) {
        return isNotEmpty(nvlObj(obj, ""));
    }

    /**
     * in절 구현
     * 배열형 문자열의 값중 하나라도 포함된 경우 true 리턴
     * 하나도 포함되지 않은 경우 false 리턴
     *
     * @param str
     * @param compares
     * @return true/false
     */
    public static boolean in(String str, String... compares) {
        if (isNotEmpty(str) && isNotEmptyObj(compares)) {
            for (String compare : compares) {
                if (isNotEmpty(compare)) {
                    if (str.equals(compare)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean in(String str, List<String> compares) {
        return in(str, compares.toArray(new String[0]));
    }

    /**
     * not in절 구현
     * 배열형 문자열의 값중 하나라도 포함된 경우 true 리턴
     * 하나도 포함되지 않은 경우 false 리턴
     *
     * @param str
     * @param compares
     * @return true/false
     */
    public static boolean notIn(String str, String... compares) {
        return !in(str, compares);
    }

    public static boolean notIn(String str, List<String> compares) {
        return !in(str, compares);
    }

    /**
     * 두 문자열을 비교해서 같으면 true 다르면 false 리턴
     * 
     * @param str1
     * @param str2
     * @return true/false
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return str1 == str2;
        }
        return str1.equals(str2);
    }

    /**
     * 두 Object를 받아서 String 형으로 변환후 비교해서 같으면 true 다르면 false 리턴
     *
     * @param obj1
     * @param obj2
     * @return true/false
     */
    public static boolean equalsObj(Object obj1, Object obj2) {
        return equals(nvlObj(obj1), nvlObj(obj2));
    }
    
    /**
     * 두 문자열을 비교해서 다르면 true 같으면 false 리턴
     *
     * @param str1
     * @param str2
     * @return true/false
     */
    public static boolean isNotEquals(String str1, String str2) {
        return !equals(str1, str2);
    }

    /**
     * 두 Object를 받아서 String 형으로 변환후 비교해서 다르면 true 같으면 false 리턴
     *
     * @param obj1
     * @param obj2
     * @return true/false
     */
    public static boolean isNotEqualsObj(Object obj1, Object obj2) {
        return !equalsObj(obj1, obj2);
    }

    /**
     * 문자열과 입력받아 숫자에 해당하는 값만 출력 하는 기능
     *
     * @param str
     * @return String
     */
    public static String numberValue(String str) {
        return regexValue(str, NUM);
    }

    public static String numberValueObj(Object str) {
        return numberValue(str.toString());
    }

    /**
     * 문자열과 입력받아 영어에 해당하는 값만 출력 하는 기능
     *
     * @param str
     * @return String
     */
    public static String engValue(String str) {
        return regexValue(str, ENG);
    }

    public static String engValueObj(Object str) {
        return engValue(str.toString());
    }

    
    /**
     * 숫자
     *
     * @param obj
     * @return String
     */
    public static String removeDecimalPointToZeroObj(Object obj) {
        DecimalFormat format = new DecimalFormat("#.######");
        if (obj instanceof Number) {
            return format.format(obj);
        } else if (obj instanceof String) {
            if (hasFindByRegex(NUMERIC, (String) obj)) {
                return format.format(Double.valueOf((String) obj));
            }
        }
        return null;
    }

    public static String removeDecimalPointToZero(Double number) {
        if (number == null) {
            return null;
        }
        DecimalFormat format = new DecimalFormat("#.######");
        return format.format(number);
    }

    public static String removeBlankAndDash(String str) {
        str = isEmpty(str) ? "" : str;
        return str.replaceAll("\\p{Z}", "").replaceAll("-", "");
    }

    public static String replaceBlank(String str) {
        str = isEmpty(str) ? "" : str;
        return str.replaceAll("\\p{Z}", "");
    }



    public static String concat(String str, String... appends) {
        str = isEmpty(str) ? "" : str;
        if (appends == null || appends.length <= 0) {
            return str;
        }
        for (String append : appends) {
            str = concat(str, append);
        }
        return str;
    }

    public static String concat(String str, String append) {
        return nvl(str).concat(nvl(append));
    }
    
    
    public static String substring(String str, int start, int end) {
        str = isEmpty(str) ? "" : str;
        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start > end) {
            return "";
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }
    
    public static String substringStart(String str, int start) {
        return substring(str, start, str.length());
    }
    
    public static String substringEnd(String str, int end) {
        return substring(str, 0, str.length() - end);
    }

    /**
     * 문자열과 해당 정규식을 입력받아 정규에 해당하는 값만 출력 하는 기능
     *
     * @param str
     * @param regex
     * @return String
     */
    public static String regexValue(String str, String regex) {
        if (str == null || regex == null) {
            return null;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        String result = "";
        while (matcher.find()) {
            result = result.concat(matcher.group(1));
        }
        return result;
    }

    public static List<String> regexValues(String str, String regex) {
        if (str == null || regex == null) {
            return null;
        }
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }

    /**
     * target 문자열이 정규식 패턴과 일치하면 true, 불일치하면 false
     *
     * @param regExp
     * @param target
     * @return true/false
     */
    public static boolean hasFindByRegex(String regExp, String target) {
        if (isEmpty(target)) {
            return false;
        }
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher((String) target);
        return matcher.find();
    }
    
    /**
     * url 패턴 체크
     * @param url
     * @param pattern
     * @return
     */
    public static boolean urlPatternCheck(String url, String pattern) {
        if (url == null || pattern == null) {
            return url == pattern;
        }
        if (pattern.endsWith("/**")) {
            return StringUtil.hasFindByRegex(pattern.replace("/**", "(/)?.*"), url);
        } else if (pattern.contains("**")) {
            return StringUtil.hasFindByRegex(pattern.replace("**", ".*"), url);
        }
        return pattern.equals(url);
    }
    
    public static boolean urlPatternCheck(String url, Set<String> patterns) {
        for (String pattern : patterns) {
            if (urlPatternCheck(url, pattern)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 입력한 모든 값이 빈문자열이 아니면 true, 빈문자열이 하나라도 있으면 false
     *
     * @param strs
     * @return
     */
    public static boolean isNotEmptyAll(String... strs) {
        for (String arg : strs) {
            if (isEmpty(arg)) {
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * 문자열 str1과 str2 사전식 크기비교 함수
     * str1 < str2 : -
     * str1 = str2 : 0
     * str1 > str2 : +
     * null, str2 : -1
     * str1, null : +1
     * null, null : 0
     * @param str1
     * @param str2
     * @return
     */
    public static int compare(String str1, String str2) {
        return nvl(str1).compareTo(nvl(str2));
    }
    
    public static boolean goe(String str1, String str2) {
        if (compare(str1, str2)>= 0) {
            return true;
        }
        return false;
    }
    public static boolean loe(String str1, String str2) {
        if (compare(str1, str2)<= 0) {
            return true;
        }
        return false;
    }
    public static boolean gt(String str1, String str2) {
        return !loe(str1, str2);
    }
    public static boolean lt(String str1, String str2) {
        return !goe(str1, str2);
    }


    public static String trimObj(Object obj) {
        return obj == null ? null : obj.toString().trim();
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }
    
    
    /**
     * 문자 배열 사이에 구분자를 추가하여 문자열 반환
     * @param delimiter 구분자
     * @param strs 문자 배열
     * @return
     */
    public static String join(String delimiter, String... strs) {
        return String.join(delimiter, strs);
    }

    public static String join(String delimiter, List<String> strs) {
        return String.join(delimiter, strs);
    }
    
    
    /**
     * 랜덤한 영문 대/소문자, 숫자로 구성된 문자열 반환
     * @param length
     * @return
     */
    public static String randomString(int length) { 
        StringBuffer temp = new StringBuffer();
        Random rnd = new Random();
        for (int i = 0; i < length; i++) {
            int rIndex = rnd.nextInt(3);
            switch (rIndex) {
                case 0:
                    // 0-9
                    temp.append((rnd.nextInt(10)));
                    break;
                case 1:
                    // a-z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 97));
                    break;
                case 2:
                    // A-Z
                    temp.append((char) ((int) (rnd.nextInt(26)) + 65));
                    break;
            }
        }
        return temp.toString();
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    
}