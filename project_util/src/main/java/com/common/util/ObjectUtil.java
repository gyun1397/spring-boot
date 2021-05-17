package com.common.util;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.common.converter.CustomConverter;
import com.common.converter.deserializer.CustomDateDeserializer;
import com.common.converter.deserializer.CustomLocalDateDeserializer;
import com.common.converter.deserializer.CustomLocalDateTimeDeserializer;
import com.common.converter.serializer.CustomDateSerializer;
import com.common.converter.serializer.CustomLocalDateSerializer;
import com.common.converter.serializer.CustomLocalDateTimeSerializer;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
public class ObjectUtil {
    private static final ObjectMapper    objectMapper    = new ObjectMapper();
    private static final ModelMapper     modelMapper     = new ModelMapper();
    private static final CustomConverter customConverter = new CustomConverter();
    static {
        BeanUtilsBean.setInstance(new BeanUtilsBean(new ConvertUtilsBean() {
            @Override
            public Object convert(String value, Class clazz) { // map-> object화(populate)할때, enum타입으로 제대로 변경하기 위함
                if (clazz.isEnum()) {
                    return Enum.valueOf(clazz, value);
                } else {
                    return super.convert(value, clazz);
                }
            }
        }));
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);    
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Date.class, new CustomDateSerializer());
        simpleModule.addSerializer(LocalDate.class, new CustomLocalDateSerializer());
        simpleModule.addSerializer(LocalDateTime.class, new CustomLocalDateTimeSerializer());
        simpleModule.addDeserializer(Date.class, new CustomDateDeserializer());
        simpleModule.addDeserializer(LocalDate.class, new CustomLocalDateDeserializer());
        simpleModule.addDeserializer(LocalDateTime.class, new CustomLocalDateTimeDeserializer());
        objectMapper.registerModule(simpleModule);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        ConvertUtils.register(customConverter, Date.class);
        ConvertUtils.register(customConverter, Timestamp.class);
        ConvertUtils.register(customConverter, LocalDate.class);
        ConvertUtils.register(customConverter, LocalDateTime.class);
        ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
    }

    public static ObjectMapper getInstance() {
        return objectMapper;
    }

    /**
     * <T> 타입의 Object를 같은 타입의 Object로 복제함.
     * 
     * @param obj
     * @return
     */
    public static <T> T cloneBeans(T obj) {
        if (obj == null) {
            return null;
        }
        T t = null;
        try {
            t = (T) org.apache.commons.beanutils.BeanUtils.cloneBean(obj);
        } catch (InstantiationException e) {
            new IllegalArgumentException("Cannot initiate class", e);
        } catch (IllegalAccessException e) {
            new IllegalStateException("Cannot access the property", e);
        } catch (InvocationTargetException e) {
            new IllegalArgumentException(e);
        } catch (NoSuchMethodException e) {
            new IllegalArgumentException(e);
        }
        return t;
    }

    /**
     * <T> 타입의 List를 같은 타입의 List로 복제함.
     * 
     * @param entities
     * @return
     */
    public static <T> List<T> cloneLists(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>();
        for (T entity : entities) {
            result.add(cloneBeans(entity));
        }
        return result;
    }

    /**
     * obj를 <T> classType 의 Object로 바인딩 함
     * 
     * @param obj
     * @param classType
     * @return
     */
    public static <T> T convertObject(Object obj, Class<T> classType) {
        if (obj == null) {
            return null;
        }
        return (T) modelMapper.map(obj, classType);
    }

    /**
     * obj를 <T> classType 의 Object로 바인딩 함
     * 
     * @param obj
     * @param classType
     * @return
     */
    public static <T> T convertBean(Object obj, Class<T> classType) throws Exception {
        T t = classType.getDeclaredConstructor().newInstance();
        BeanUtilsBean.getInstance().copyProperties(t, obj);
        return t;
    }

    /**
     * <C> initClass의 클래스로 초기화 후 다시 <T> 타입의 obj로 바인딩
     * 
     * @param obj
     * @param initClass
     * @param classType
     * @return
     */
    public static <T, C> T initObject(T obj, Class<C> initClass) throws Exception {
        return (T) convertObject(convertObject(obj, initClass), obj.getClass());
    }

    /**
     * <C> initClass의 클래스로 초기화 후 <T2> 타입의 targetClass 로 바인딩
     * 
     * @param obj
     * @param initClass
     * @param classType
     * @return
     */
    public static <T1, T2, C> T2 initObject(T1 obj, Class<C> initClass, Class<T2> targetClass) throws Exception {
        return convertObject(convertObject(obj, initClass), targetClass);
    }

    /**
     * Collection 객체를 <T> classType의 Collection 객체로 바인딩 함.
     * 
     * @param collection
     * @param classType
     * @return
     */
    public static <T> List<T> convertCollection(Collection<?> collection, Class<T> classType) {
        if (collection == null) {
            return null;
        }
        return collection.stream()
                .map(object -> {
                    try {
                        return convertBean(object, classType);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }

    public static <T> List<T> convertListValue(List<? extends Object> objList, Class<T> classType, String key) {
        if (objList == null) {
            return null;
        }
        List<T> tList = new ArrayList<T>();
        for (Object obj : objList) {
            try {
                Object value = PropertyUtils.getProperty(obj, key);
                if (value != null) {
                    tList.add((T) value);   
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tList;
    }

    /**
     * Iterable 객체를 <T> classType의 Iterable 객체로 바인딩 함.
     * 
     * @param iterable
     * @param classType
     * @return
     */
    public static <T> List<T> convertIterable(Iterable<?> iterable, Class<T> classType) {
        if (iterable == null) {
            return null;
        }
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(object -> {
                    try {
                        return convertBean(object, classType);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }

    /**
     * Array 객체를 <T> classType의 Array 객체로 바인딩 함.
     * 
     * @param array
     * @param classType
     * @return
     */
    public static <T> T[] convertArray(Object[] array, Class<T> classType) {
        if (array == null) {
            return null;
        }
        return convertCollection(Arrays.asList(array), classType).toArray((T[]) Array.newInstance(classType, array.length));
    }

    /**
     * Page 객체를 <T> classType의 Page 객체로 바인딩 함.
     * 
     * @param pages
     * @param classType
     * @return
     */
    public static <T> Page<T> convertPage(Page<?> pages, Class<T> classType) {
        if (pages == null) {
            return null;
        }
        List<T> list = pages.getContent().stream()
                .map(object -> {
                    try {
                        return convertBean(object, classType);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
        return new PageImpl<>(list, pages.getPageable(), pages.getTotalElements());
    }

    /**
     * <T> 타입의 Object에 특정 Object 값을 바인딩 함
     * 
     * @param dest
     *            값이 바뀌는 대상 객체
     * @param orig
     *            바인딩 데이터 객체
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static <T> T copyProperties(T dest, Object orig)
            throws IllegalAccessException, InvocationTargetException {
        BeanUtils.copyProperties(orig, dest);
        return dest;
    }

    /**
     * <T> 타입의 Object에 특정 Object 값을 바인딩 함
     * 단, 특정 Object에 서 null값은 적용 시키지 않는다.
     * 
     * @param dest
     * @param orig
     * @return
     */
    public static <T> T copyPropertiesIgnoreNull(T dest, Object orig) {
        final BeanWrapper src = new BeanWrapperImpl(orig);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();
        Set<String> validNames = new HashSet<String>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null)
                validNames.add(pd.getName());
        }
        String[] result = new String[validNames.size()];
        BeanUtils.copyProperties(orig, dest, validNames.toArray(result));
        return dest;
    }

    /**
     * <T> 타입의 Object에 Map의 값을 바인딩 함
     * 
     * @param obj
     * @param map
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static <T> T populate(T obj, Map<String, Object> map)
            throws IllegalAccessException, InvocationTargetException {
        BeanUtilsBean.getInstance().populate(obj, map);
        return obj;
    }

    /**
     * <T> 타입의 Iterabler 객체를 Page 객체로 전환.
     * 
     * @param iterable
     * @param pageable
     * @return
     */
    public static <T> Page<T> convertIterableToPage(Iterable<T> iterable, Pageable pageable) {
        if (!iterable.iterator().hasNext()) {
            return null;
        }
        return convertListToPage(convertIterableToList(iterable), pageable);
    }

    /**
     * <T> 타입의 List 객체를 Page 객체로 전환.
     * 
     * @param list
     * @param pageable
     * @return
     */
    public static <T> Page<T> convertListToPage(List<T> list, Pageable pageable) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<T> result = new ArrayList<>();
        int idx = pageable.getPageNumber() * pageable.getPageSize();
        for (int i = 0; i < pageable.getPageSize(); i++) {
            if (idx >= list.size()) {
                break;
            }
            result.add(list.get(idx));
            idx++;
        }
        return new PageImpl<>(result, pageable, list.size());
    }

    public static <T> Page<T> convertListToPage(List<T> list, int page, int size) {
        return convertListToPage(list, PageRequest.of(page, size));
    }

    public static <T> Page<T> convertListToPage(List<T> list, int page, int size, long count) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return new PageImpl<>(list, PageRequest.of(page, size), count);
    }

    /**
     * <T> 타입의 Iterable 객체를 List 객체로 전환.
     * 
     * @param iterable
     * @return
     */
    public static <T> List<T> convertIterableToList(Iterable<T> iterable) {
        if (iterable == null || !iterable.iterator().hasNext()) {
            return new ArrayList<>();
        }
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }
    
    /**
     * <T> 타입의 Array 객체를 List 객체로 전환.
     * 
     * @param <T>
     * @param array
     * @return
     */
    public static <T> List<T> convertArrayToList(T[] array) {
        if (array == null) {
            return new ArrayList<>();
        }
        return new ArrayList<T>(Arrays.asList(array));
    }
    
    public static <T> T[] convertListToArray(List<T> collection, Class<T> classType) {
        if (collection == null) {
            return null;
        }
        return  collection.toArray((T[]) Array.newInstance(classType, collection.size()));
    }

    /**
     * List<Map> 형태의 객체를 List<C> 형태의 객체로 전환.
     * 
     * @param list
     * @param clazz
     * @return
     */
    public static <T extends Map<String, Object>, C> List<C> convertMapToBean(List<T> list, Class<C> clazz) {
        List<C> beanList = new ArrayList<C>();
        for (Map<String, Object> source : list) {
            C bean = convertBean(source, clazz);
            beanList.add(bean);
        }
        return beanList;
    }

    /**
     * Map 객체를 <C> targetClass 타입으로 전환.
     * 
     * @param source
     * @param targetClass
     * @return
     */
    public static <C> C convertBean(Map<String, Object> source, Class<C> targetClass) {
        C bean = null;
        try {
            bean = targetClass.getDeclaredConstructor().newInstance();
            PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(targetClass);
            for (PropertyDescriptor desc : targetPds) {
                Object value = source.get(desc.getName());
                if (value != null) {
                    Method writeMethod = desc.getWriteMethod();
                    if (writeMethod != null) {
                        writeMethod.invoke(bean, new Object[] { value });
                    }
                }
            }
        } catch (InstantiationException e) {
            new IllegalArgumentException("Cannot initiate class", e);
        } catch (IllegalAccessException e) {
            new IllegalStateException("Cannot access the property", e);
        } catch (Exception e) {
            new IllegalArgumentException(e);
        }
        return bean;
    }

    /**
     * Entity Validation Check 용 변환 기능
     * update 동작시 map 형태의 body를 받아 해당 객체로 변환을 통해
     * Type Mismatch Field 를 찾기 위해 사용
     * 
     * @param map
     * @param targetClass
     * @return
     */
    public static <C> C convertMap(Map<String, Object> map, Class<C> targetClass) {
        return objectMapper.convertValue(map, targetClass);
    }

    /**
     * 특정 Object의 파라미터를 Map 형태로 전환.
     * 
     * @param obj
     * @return
     */
    public static Map<String, Object> convertMap(Object obj) {
        if (obj instanceof Map<?, ?>) {
            return (Map<String, Object>) obj;
        }
        return objectMapper.convertValue(obj, Map.class);
    }

    /**
     * 특정 Object의 파라미터를 Map 형태로 전환. deepCopy
     * 
     * @param obj
     * @return
     */
    public static Map<String, Object> convertMapDeepCopy(Object obj) {
        Class<?> objectClass = obj.getClass();
        if (obj instanceof Map<?, ?>) {
            return (Map<String, Object>) obj;
        }
        Map<String, Object> map = new HashMap<>();
        do {
            Field[] fields = objectClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                try {
                    map.put(fields[i].getName(), fields[i].get(obj));
                } catch (IllegalArgumentException e) {
                    new IllegalArgumentException("Cannot initiate class", e);
                } catch (IllegalAccessException e) {
                    new IllegalStateException("Cannot access the property", e);
                }
            }
        } while ((objectClass = objectClass.getSuperclass()) != null);
        return map;
    }

    /**
     * 특정 Object의 파라미터를 Map 형태로 전환. 1 depth copy
     * 
     * @param obj
     * @return
     */
    public static Map<String, Object> describe(Object obj) {
        Map<String, Object> map = null;
        try {
            map = PropertyUtils.describe(obj);
        } catch (NoSuchMethodException e) {
            new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            new IllegalStateException("Cannot access the property", e);
        } catch (InvocationTargetException e) {
            new IllegalArgumentException(e);
        }
        return map;
    }

    /**
     * obj를 <T> toValueType으로 변환한다. 이때 obj의 소문자를 대문자로 변환하여 해당 대문자에 맵핑되는 멤버필드와 바인딩함.
     * 
     * @param obj
     * @param toValueType
     * @return
     */
    public static <T> T convertUpperValue(Object obj, Class<T> toValueType) {
        /**
         * - 맵핑안되는 필드는 무시하도록 하는설정 - 1. 해당 객체에 설정하는 방법 ex)
         * 
         * @JsonIgnoreProperties(ignoreUnknown = true) public class CmmnDetailCode
         *                                     implements Serializable {
         * 
         *                                     2. mapper에 설정하는 방법 Jackson version 1.9 or
         *                                     earlier ex)
         *                                     mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
         *                                     false);
         * 
         *                                     Jackson 2.0 or later ex)
         *                                     mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
         *                                     false);
         */
        // mapper.setPropertyNamingStrategy(new LowerCaseNamingStrategy());
        return objectMapper.convertValue(obj, toValueType);
    }

    /**
     * obj를 <T>toValueType으로 변환한다. 이때 obj의 소문자를 setter/getter 네이밍에 맞게 변환하여 해당 대문자에
     * 맵핑되는 멤버필드와 바인딩함.
     * 
     * @param obj
     * @param toValueType
     * @return
     */
    public static <T> T convertValue(Object obj, Class<T> toValueType) {
        // mapper.setPropertyNamingStrategy(PropertyNamingStrategy. new
        // LowerCaseWithUnderscoresStrategy()); //
        return objectMapper.convertValue(obj, toValueType);
    }

    /**
     * List<변환전 타입> 객체을 List<변환후 타입> 객체로 맵핑해 주는 기능
     * 
     * @param obj
     * @param typeInList
     * @return
     */
    public static <T> List<T> convertListValue(List<? extends Object> objList, Class<T> typeInList) {
        List<T> tList = new ArrayList<T>();
        for (Object obj : objList) {
            tList.add(objectMapper.convertValue(obj, typeInList));
        }
        return tList;
    }

    /**
     * Map 내 value에 대해 모두 추출함
     * 
     * @param obj
     * @param typeInList
     * @return
     */
    public static <T> List<T> getMapValues(Map<String, T> map, Class<T> type) {
        return new ArrayList<T>(map.values());
    }

    /**
     * Map 내 key에 대해 모두 추출함
     * 
     * @param obj
     * @param typeInList
     * @return
     */
    public static <T> List<String> getKeyValues(Map<String, T> map, Class<T> type) {
        return new ArrayList<String>(map.keySet());
    }

    /**
     * List<변환전 타입> 객체을 Map<String,변환후 타입> 객체로 맵핑해 주는 기능 , 단 이때 Map의 Key값은 파라미터를
     * 주어야함. 이때 key를 multiple 하게 주고자할경우, 구분자(/)를 이용하면 된다.
     * 
     * @param obj
     * @param voType
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <T> Map<String, T> convertMapValue(List<? extends Object> objList, Class<T> voType, String key)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Map<String, T> tMap = new HashMap<String, T>();
        String[] joinKeys = null;
        String joinKey = "";
        for (Object obj : objList) {
            // mapper.setPropertyNamingStrategy(new LowerCaseWithUnderscoresStrategy());
            if (key.contains("/")) {
                joinKeys = key.split("/");
                joinKey = "";
                for (String tmpKey : joinKeys) {
                    joinKey += (String) PropertyUtils.getProperty(obj, tmpKey);
                }
                if (joinKey != null) {
                    tMap.put(joinKey, objectMapper.convertValue(obj, voType));
                }
            } else {
                String tmpKey = (String) PropertyUtils.getProperty(obj, key);
                if (tmpKey != null) {
                    tMap.put(tmpKey, objectMapper.convertValue(obj, voType));
                }
            }
        }
        return tMap;
    }

    public static <T1, T2> Map<T1, T2> convertMapValue(List<? extends Object> objList, Class<T1> keyType,
            Class<T2> valueType, String keyField)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Map<T1, T2> tmpMap = new HashMap<T1, T2>();
        for (Object obj : objList) {
            T1 key = (T1) PropertyUtils.getProperty(obj, keyField);
            tmpMap.put(key, objectMapper.convertValue(obj, valueType));
        }
        return tmpMap;
    }


    /**
     * obj를 json 문자열형태로 변환해줌.
     * 
     * @param obj
     * @return
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static String writeValueAsString(Object obj)
            throws JsonGenerationException, JsonMappingException, IOException {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * obj를 json 문자열형태로 변환해줌. PrettyPrinter
     * 
     * @param obj
     * @return
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static String writeValueAsStringPretty(Object obj)
            throws JsonGenerationException, JsonMappingException, IOException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    /**
     * json 문자열을 읽어서 <T>타입의 Object 객체로 읽어들여 반환
     * 
     * @param content
     * @param valueType
     * @return
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static <T> T readValue(String content, Class<T> valueType)
            throws JsonGenerationException, JsonMappingException, IOException {
        return objectMapper.readValue(content, valueType);
    }

    public static Map<String, Object> readValueToMap(String content)
            throws JsonGenerationException, JsonMappingException, IOException {
        return objectMapper.readValue(content, Map.class);
    }

    /**
     * 
     * @param jsontStr
     * @param key
     * @return
     */
    public static String readValueInMap(String jsontStr, String key) {
        try {
            Map<String, Object> map = objectMapper.readValue(jsontStr, Map.class);
            if (map != null) {
                if (StringUtil.isNotEmptyObj(map.get(key))) {
                    return map.get(key).toString();
                }
            }
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
        return null;
    }

    /**
     * json 문자열을 읽어서 <T>타입의 List 객체로 읽어들여 반환
     * 
     * @param content
     * @param valueType
     * @return
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static <T> List<T> readValueToList(String content, Class<T> valueType)
            throws JsonGenerationException, JsonMappingException, IOException {
        return objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(List.class, valueType));
    }

    /**
     * obj를 json 문자열로 변환
     * 
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    public static String convertObjectToJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * URL 요청에 해당하는 결과를 Map 형태의 객체로 받는다.(get 방식만 가능함)
     * 
     * @param url
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static Map<String, Object> getJson(String urlStr)
            throws JsonParseException, JsonMappingException, IOException {
        return (Map<String, Object>) objectMapper.readValue(new URL(urlStr), Map.class);
    }

    public static Map<String, Object> getJson(String urlStr, Map<String, Object> params)
            throws JsonParseException, JsonMappingException, IOException {
        List<NameValuePair> list = convertParam(params);
        urlStr = urlStr + "?" + URLEncodedUtils.format(list, "UTF-8");
        log.debug("최종 GET방식 요청 URL 주소 : {}", urlStr);
        return (Map<String, Object>) objectMapper.readValue(new URL(urlStr), Map.class);
    }

    public static List<NameValuePair> convertParam(Map<String, Object> params) {
        List<NameValuePair> paramList = new ArrayList<NameValuePair>();
        Iterator<String> keys = params.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            paramList.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
        return paramList;
    }
    
    public static <T> Map<String, String> getSchema(Class<T> classType) {
        Map<String, String> schema = new HashMap<>();
        Class<?> res = classType;
        do {
            for (Field field : res.getDeclaredFields()) {
                if ("id".equals(field.getName())) {
                    schema.put(field.getName(), "ID");
                    continue;
                }
                schema.put(field.getName(), field.getType().getSimpleName());
            }
            res = res.getSuperclass();
            
        } while (res != null);
        return schema;
    }
    
    /**
     * 두 Object간 동치 비교
     * 
     * @param obj1
     * @param obj2
     * @return
     */
    public static boolean equals(Object obj1, Object obj2) {
        return obj1 == null ? obj2 == null : obj1.equals(obj2);
    }


    public static <T> T nvl(T obj, T obj2) {
        obj = obj2;
        return obj;
    }

}