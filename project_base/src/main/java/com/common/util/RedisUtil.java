package com.common.util;

import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class RedisUtil {

    private static StringRedisTemplate stringRedisTemplate = null;

    public static String getData(String key) {
        init();
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public static void setData(String key, String value) {
        init();
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set(key,value);
    }

    public static void setDataExpire(String key, String value, long duration) {
        init();
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        Duration expireDuration = Duration.ofMillis(duration);
        valueOperations.set(key,value,expireDuration);
    }
    
    public static boolean hasKey(String key) {
        init();
        return stringRedisTemplate.hasKey(key);
    }
    
    public static void deleteData(String key) {
        init();
        stringRedisTemplate.delete(key);
    }
    
    private static void init() {
        if (stringRedisTemplate == null) {
            stringRedisTemplate = BeanUtil.createService(StringRedisTemplate.class);
        }
    }
    
    public static void setRedisTemplate(StringRedisTemplate templat) {
        stringRedisTemplate = templat;
    }

    
}
