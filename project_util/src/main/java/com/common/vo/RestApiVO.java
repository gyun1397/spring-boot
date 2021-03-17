package com.common.vo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import com.common.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.Data;

@Data
@SuppressWarnings("unchecked")
public class RestApiVO {
    private Integer             status;
    private String              resultJson;
    private HttpResponse        response;
    private Map<String, Object> jsonObj = null;

    public RestApiVO() {
        this(null, 999, null);
    }

    public RestApiVO(Integer status, String resultJson) {
        this(null, status, resultJson);
    }

    public RestApiVO(HttpResponse response, Integer status, String resultJson) {
        this.response = response;
        this.status = status;
        this.resultJson = resultJson;
    }

    public boolean isSuccess() {
        if (this.status >= 200 && this.status <= 299) {
            return true;
        }
        return false;
    }

    public boolean isFail() {
        return !isSuccess();
    }

    /**
     * key를 이용해 1depth의 value를 찾아옴
     * 
     * @return
     */
    public Object getValue(String key) {
        if (jsonObj == null) {
            try {
                jsonObj = ObjectUtil.readValue(resultJson, Map.class);
            } catch (JsonGenerationException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                jsonObj = (jsonObj != null ? jsonObj : new HashMap<String, Object>());
            }
        }
        return jsonObj.get(key);
    }

    /**
     * key를 이용해 1,2depth의 value를 찾아옴
     * 
     * @return
     */
    public Object getValue(String key1, String key2) {
        if (jsonObj == null) {
            try {
                jsonObj = ObjectUtil.readValue(resultJson, Map.class);
            } catch (JsonGenerationException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                jsonObj = (jsonObj != null ? jsonObj : new HashMap<String, Object>());
            }
        }
        if (jsonObj.get(key1) instanceof Map) {
            try {
                return ((Map<String, Object>) jsonObj.get(key1)).get(key2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonObj.get(key1);
    }
}
