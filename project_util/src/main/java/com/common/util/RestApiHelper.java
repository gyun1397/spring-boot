package com.common.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import com.common.vo.PageVO;
import com.common.vo.RestApiVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestApiHelper {

    public enum HttpMethodType {
        POST,
        GET,
        DELETE,
        PATCH
    }

    public static RestApiVO get(String requestURL) {
        String body = "";
        Integer status = 999;
        HttpResponse response = null;
        try {
            HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
            HttpGet request = new HttpGet(requestURL); // GET 메소드 URL 생성
            response = client.execute(request);
            body = handleResponse("", request, response);// Response 출력
            status = response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            log.error(e.toString());
        }
        return new RestApiVO(response, status, body);
    }

    public static RestApiVO get(String requestURL, Map<String, Object> params) {
        return get(requestURL + getParams(params));
    }
    
    public static RestApiVO get(String requestURL, String jwt) {
        String body = "";
        Integer status = 999;
        HttpResponse response = null;
        try {
            HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
            HttpGet request = new HttpGet(requestURL); // GET 메소드 URL 생성
            if (StringUtil.isNotEmpty(jwt)) {
                addHeader(request, jwt);
            }
            response = client.execute(request);
            body = handleResponse("", request, response);// Response 출력
            status = response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            log.error(e.toString());
        }
        return new RestApiVO(response, status, body);
    }

    public static RestApiVO get(String requestURL, String jwt, Map<String, Object> params) {
        return get(requestURL + getParams(params), jwt);
    }

    public static String getResult(String requestURL, String jwt) {
        RestApiVO restApiVO = get(requestURL, jwt);
        return restApiVO.getResultJson();
    }

    public static String getResult(String requestURL) {
        return getResult(requestURL, null);
    }

    public static int getStatue(String requestURL, String jwt) {
        Integer status = 999;
        try {
            HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
            HttpGet request = new HttpGet(requestURL); // GET 메소드 URL 생성
            if (StringUtil.isNotEmpty(jwt)) {
                addHeader(request, jwt);
            }
            HttpResponse response = client.execute(request);
            status = response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            log.error(e.toString());
        }
        return status;
    }

    public static int getStatue(String requestURL) {
        return getStatue(requestURL, null);
    }

    /**
     * POST 요청(JWT토큰이 없을시, null)
     *
     * @param requestURL
     * @param jsonStr
     * @param jwt
     * @return
     */
    public static RestApiVO post(String requestURL, String jsonStr) {
        return post(requestURL, null, jsonStr);
    }

    /**
     * POST 요청(JWT토큰이 없을시, null)
     *
     * @param requestURL
     * @param jsonStr
     * @param jwt
     * @return
     */
    public static RestApiVO post(String requestURL, String jwt, String jsonStr) {
        String body = "";
        Integer status = 999;
        HttpResponse response = null;
        try {
            HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
            HttpPost request = new HttpPost(requestURL); // POST 메소드 URL 새성
            if (StringUtil.isNotEmpty(jwt)) {
                addHeader(request, jwt);
            }
            request.setEntity(new StringEntity(jsonStr, ContentType.APPLICATION_JSON)); // json 메시지 입력
            response = client.execute(request);
            body = handleResponse(jsonStr, request, response);// Response 출력
            status = response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            log.error(e.toString());
        }
        return new RestApiVO(response, status, body);
    }

//    /**
//     * Multipart POST 요청
//     *
//     * @param requestURL
//     * @param jsonStr
//     * @param jwt
//     * @return
//     */
//    public static RestApiVO multipartPost(String requestURL, Map<String, Object> map, File[] files) {
//        return multipartPost(requestURL, null, map, files);
//    }
//    
//    /**
//     * Multipart POST 요청(JWT토큰이 없을시, null)
//     *
//     * @param requestURL
//     * @param jsonStr
//     * @param jwt
//     * @return
//     */
//    public static RestApiVO multipartPost(String requestURL, String jwt, Map<String, Object> map, File[] files) {
//        String body = "";
//        Integer status = 999;
//        CloseableHttpResponse response = null;
//        try {
//            CloseableHttpClient httpClient = HttpClients.createDefault();
//            HttpPost request = new HttpPost(requestURL);
//            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//            builder.setContentType(ContentType.MULTIPART_FORM_DATA);
//            if (StringUtil.isNotEmpty(jwt)) {
//                addHeader(request, jwt);
//            }
//            Set<String> keys = map.keySet();
//            for (String key : keys) {
//                if (StringUtil.isNotEmptyObj(map.get(key))) {
//                    builder.addTextBody(key, StringUtil.stringConvert(map.get(key)), ContentType.TEXT_PLAIN);
//                }
//            }
//            if (files != null) {
//                for (File file : files) {
//                    System.out.println(file.getName());
//                    System.out.println(MIME_TYPE.getContenType(file.getName()));
//                    builder.addBinaryBody(
//                            "files",
//                            new FileInputStream(file),
//                            ContentType.getByMimeType(MIME_TYPE.getContenType(file.getName())),
//                            file.getName()
//                            );
//                }
//            }
//
//            HttpEntity multipart = builder.build();
//            request.setEntity(multipart);
//            response = httpClient.execute(request);
////            HttpEntity responseEntity = response.getEntity();
//            
//            body = handleResponse(ObjectUtil.writeValueAsString(map), request, response);// Response 출력
//            status = response.getStatusLine().getStatusCode();
//        } catch (Exception e) {
//            log.error(e.toString());
//        }
//        return new RestApiVO(response, status, body);
//    }
    
    /**
     * PUT 요청(JWT토큰이 없을시, null)
     *
     * @param requestURL
     * @param jsonStr
     * @param jwt
     * @return
     */
    public static RestApiVO put(String requestURL, String jwt, String jsonStr) {
        String body = "";
        Integer status = 999;
        HttpResponse response = null;
        try {
            HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
            HttpPut request = new HttpPut(requestURL); // POST 메소드 URL 새성
            if (StringUtil.isNotEmpty(jwt)) {
                addHeader(request, jwt);
            }
            request.setEntity(new StringEntity(jsonStr, ContentType.APPLICATION_JSON)); // json 메시지 입력
            response = client.execute(request);
            body = handleResponse(jsonStr, request, response);// Response 출력
            status = response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            log.error(e.toString());
        }
        return new RestApiVO(response, status, body);
    }

    /**
     * PUT 요청(JWT토큰이 없을시, null)
     *
     * @param requestURL
     * @param jsonStr
     * @param jwt
     * @return
     */
    public static RestApiVO put(String requestURL, String jsonStr) {
        return put(requestURL, null, jsonStr);
    }

    /**
     * PATCH 요청(JWT토큰이 없을시, null)
     *
     * @param requestURL
     * @param jsonStr
     * @param jwt
     * @return
     */
    public static RestApiVO patch(String requestURL, String jwt, String jsonStr) {
        String body = "";
        Integer status = 999;
        HttpResponse response = null;
        try {
            HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
            HttpPatch request = new HttpPatch(requestURL); // PATCH 메소드 URL 새성
            if (StringUtil.isNotEmpty(jwt)) {
                addHeader(request, jwt);
            }
            request.setEntity(new StringEntity(jsonStr, ContentType.APPLICATION_JSON)); // json 메시지 입력
            response = client.execute(request);
            body = handleResponse(jsonStr, request, response);// Response 출력
            status = response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            log.error(e.toString());
        }
        return new RestApiVO(response, status, body);
    }

    /**
     * PATCH 요청(JWT토큰이 없을시, null)
     *
     * @param requestURL
     * @param jsonStr
     * @param jwt
     * @return
     */
    public static RestApiVO patch(String requestURL, String jsonStr) {
        return patch(requestURL, null, jsonStr);
    }

    /**
     * DELETE 요청(JWT토큰이 없을시, null)
     *
     * @param requestURL
     * @param jwt
     * @return
     */
    public static RestApiVO delete(String requestURL, String jwt) {
        String body = "";
        Integer status = 999;
        try {
            HttpClient client = HttpClientBuilder.create().build(); // HttpClient 생성
            HttpDelete request = new HttpDelete(requestURL); // DELETE 메소드 URL 새성
            if (StringUtil.isNotEmpty(jwt)) {
                addHeader(request, jwt);
            }
            HttpResponse response = client.execute(request);
            body = handleResponse("", request, response);// Response 출력
            status = response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            log.error(e.toString());
        }
        return new RestApiVO(status, body);
    }

    /**
     * DELETE 요청(JWT토큰이 없을시, null)
     *
     * @param requestURL
     * @param jwt
     * @return
     */
    public static RestApiVO delete(String requestURL) {
        return delete(requestURL, null);
    }

    private static void addHeader(HttpUriRequest request, String jwt) {
        request.setHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());
        request.setHeader("Connection", "keep-alive");
        request.setHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
        if (StringUtil.isNotEmpty(jwt)) {
            request.addHeader("Authorization", "Bearer " + jwt); // KEY 입력
        }
    }

    private static String handleResponse(String jsonStr, HttpUriRequest request, HttpResponse response) throws ClientProtocolException, IOException {
        log.error("[추후삭제해야함-요청] {} {} {} Headers : {}", request.getMethod(), request.getURI(), jsonStr, request.getAllHeaders());
        String body = null;
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode >= 200 && statusCode < 300) {
            ResponseHandler<String> handler = new BasicResponseHandler();
            body = handler.handleResponse(response);
        } else {
            body = EntityUtils.toString(response.getEntity());
        }
        log.error("[추후삭제해야함-응답] {} {} {}", request.getProtocolVersion(), statusCode, body.length() > 100 ? body.substring(0, 100) : body);
        return body;
    }

    public static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * 직접 입력한 String을 param으로 변환시 URLEncodeing 처리
     * @param map
     * @return
     */
    public static String mapToParams(Map<String, String> map) {
        StringBuilder paramBuilder = new StringBuilder();
        for (String key : map.keySet()) {
            paramBuilder.append(paramBuilder.length() > 0 ? "&" : "");
            paramBuilder.append(String.format("%s=%s", urlEncodeUTF8(key), urlEncodeUTF8(map.get(key).toString())));
        }
        return paramBuilder.toString();
    }

    /**
     * 
     * @param map
     * @return
     */
    public static String getParams(Map<String, Object> map) {
        StringBuilder paramBuilder = new StringBuilder();
        for (String key : map.keySet()) {
            paramBuilder.append(paramBuilder.length() > 0 ? "&" : "");
            paramBuilder.append(String.format("%s=%s", key, urlEncodeUTF8(StringUtil.nvlObj(map.get(key)))));
        }
        return paramBuilder.toString();
    }
    
    
    public static <T> T getContent(String resultJson, Class<T> returnType) {
        T t = null;
        try {
            JSONObject jsonObject = new JSONObject(resultJson);
            t = ObjectUtil.readValue(jsonObject.toString(), returnType);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return t;
    }

    public static <T> List<T> getContents(String resultJson, Class<T> returnType) {
        List<T> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(resultJson);
            JSONArray jsonArray = jsonObject.getJSONArray("content");
            list = ObjectUtil.readValueToList(jsonArray.toString(), returnType);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return list;
    }

    public static <T> Page<T> getPages(String resultJson, Class<T> returnType) {
        List<T> list = new ArrayList<>();
        PageVO pages = null;
        try {
            JSONObject jsonObject = new JSONObject(resultJson);
            JSONArray jsonArray = jsonObject.getJSONArray("content");
            list = ObjectUtil.readValueToList(jsonArray.toString(), returnType);
            JSONObject pageObject = jsonObject.getJSONObject("page");
            pages = ObjectUtil.readValue(pageObject.toString(), PageVO.class);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return new PageImpl<>(list, PageRequest.of(pages.getNumber(), pages.getSize()), pages.getTotalElements());
    }

    public static String getString(String resultJson, String key) {
        String value = null;
        try {
            JSONObject jsonObject = new JSONObject(resultJson);
            value = jsonObject.getString(key);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return value;
    }

    public static Integer getInteger(String resultJson, String key) {
        Integer value = null;
        try {
            JSONObject jsonObject = new JSONObject(resultJson);
            value = jsonObject.getInt(key);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return value;
    }
    
    public static Long getLong(String resultJson, String key) {
        Long value = null;
        try {
            JSONObject jsonObject = new JSONObject(resultJson);
            value = jsonObject.getLong(key);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return value;
    }

    public static Double getDouble(String resultJson, String key) {
        Double value = null;
        try {
            JSONObject jsonObject = new JSONObject(resultJson);
            value = jsonObject.getDouble(key);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return value;
    }
    
    public static Object getObject(String resultJson, String key) {
        Object value = null;
        try {
            JSONObject jsonObject = new JSONObject(resultJson);
            value = jsonObject.get(key);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return value;
    }

    public static void main(String[] args) throws Exception {
//        log.error("{}", RestApiHelper.post("https://new.portmis.go.kr/portmis/co/como/cnlg/selectNlgAAVsslInfo.do", "{\"dmaDtlParam\":{\"vsslInnb\":\"\",\"clsgn\":\"\",\"vsslNo\":\"\",\"imoNo\":\"8834782\",\"srchTp\":\"\"}}"));
        // if("".equals(""))return;
        // String username = "SJENG0389";
        // String jwt = TokenUtil.makeJWT("ROLE_USER_F", username, "양현석", 524, "6028119624", "선진엔텍", "F", "F");
        // log.error("jwt token : {}", jwt);
        // RestApiVO result = null;
        // try {
        // Map<String, Object> params = new HashMap<String, Object>();
        // params.put("username", username);
        // params.put("password", Const.AUTH_MASTER_KEY);
        // String jsonStr = ObjectUtil.writeValueAsString(params);
        // // String jsonStr = ObjectUtil.writeValueAsString(new Login(username, Const.AUTH_MASTER_KEY));
        // // result = RestApiHelper.post("http://localhost/api/login", null, jsonStr);
        // result = RestApiHelper.post("http://localhost/oauth/token/nic", null, jsonStr);
        // // result = RestApiHelper.post("http://dev.shipwork.net/oauth/token/nic", null, jsonStr);
        // log.error("result : {}", result);
        // // result = RestApiHelper.post("http://localhost/oauth/token/trn", jwt, jsonStr);
        // // result = RestApiHelper.getResult("http://localhost/oauth/authorize", null, jsonStr);
        // } catch (JsonGenerationException e) {
        // log.debug(e.getMessage());
        // } catch (JsonMappingException e) {
        // log.debug(e.getMessage());
        // } catch (IOException e) {
        // log.debug(e.getMessage());
        // }
        // String tempToken = ObjectUtil.readValueInMap(result.getResultJson(), "token");
        // log.error(TokenUtil.getClaim(tempToken, Const.JWT_PAYLOAD_CORP_ID));
        // log.error(TokenUtil.getClaim(tempToken, Const.JWT_PAYLOAD_ROLE));
        // log.error(TokenUtil.getClaim(tempToken, Const.JWT_PAYLOAD_USERNAME));
        // log.error(TokenUtil.getClaim(tempToken, Const.JWT_PAYLOAD_ID));
        // log.error(TokenUtil.getClaim(tempToken, Const.JWT_PAYLOAD_SERVICE_TYPE));
        // log.error(TokenUtil.getClaim(tempToken, Const.JWT_PAYLOAD_BIZ_TYPE));
        // log.error(TokenUtil.getClaim(tempToken, SYSTEM_TYPE.TRN.getSysCd()));
        // log.error(TokenUtil.getClaim(tempToken, SYSTEM_TYPE.NIC.getSysCd()));
        // log.error(TokenUtil.getClaim(tempToken, SYSTEM_TYPE.CTR.getSysCd()));
        // log.error(TokenUtil.getClaim(tempToken, SYSTEM_TYPE.BOX.getSysCd()));
        // log.error(TokenUtil.getClaim(tempToken, SYSTEM_TYPE.WCT.getSysCd()));
        // log.error("tempToken : {}", tempToken);
    }
}
