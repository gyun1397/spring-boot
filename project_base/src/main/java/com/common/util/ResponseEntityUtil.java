package com.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.common.error.Error;
import com.common.exception.CustomDefaultException;
import com.common.vo.RestApiVO;
import lombok.Getter;

public class ResponseEntityUtil {
    //
    // ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
    // ※※※※※※※ ERROR 관련 메소드 ※※※※※※※※※※※※
    // ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
    //
    /**
     * 500 서버 내부 오류
     * 
     * - 오류가 발생했을때,
     * Http Status Code : 500 ( Server Internal Error ),
     * 
     * @param e
     * @return
     */
    public static ResponseEntity<?> error(final Exception e) {
        if (e instanceof CustomDefaultException) {
            return ResponseEntityUtil.errors(((CustomDefaultException) e).getErrors());
        } else {
            return ResponseEntityUtil.error(CUSTOMS_HTTP_STATUS.INTERNAL_SERVER_ERROR.getStatus(), CUSTOMS_HTTP_STATUS.INTERNAL_SERVER_ERROR.getCode(), null, CUSTOMS_HTTP_STATUS.INTERNAL_SERVER_ERROR.title, e.getMessage());
        }
    }

    /**
     * 400 잘못된 요청 오류
     * 
     * - 오류가 발생했을때,
     * Http Status Code : 400 ( Bad Request ),
     * Custom Error Code : 9999,
     * 
     * @param error
     * @return
     */
    public static ResponseEntity<?> error(final Error error) {
        return errors(CUSTOMS_HTTP_STATUS.getTypeByCustomCode(error.getCode()).getValue(), new ArrayList<Error>() {
            {
                add(error);
            }
        });
    }

    /**
     * 400 잘못된 요청 오류 or 500 서버 내부 오류
     * 
     * - 오류가 발생했을때,
     * Custom Error Code : 9999,
     * 
     * @param httpStatus
     * @param error
     * @return
     */
    public static ResponseEntity<?> error(final Integer httpStatus, final Error error) {
        return errors(httpStatus, new ArrayList<Error>() {
            {
                add(error);
            }
        });
    }

    /**
     * 400 잘못된 요청 오류
     * 
     * - 오류가 발생했을때,
     * Http Status Code : 400 ( Bad Request ),
     * Custom Error Code : 9999,
     * field : "",
     * title : "",
     * 
     * @param message
     * @return
     */
    public static ResponseEntity<?> error(String message) {
        return error(CUSTOMS_HTTP_STATUS.DEFAULT.getValue(), CUSTOMS_HTTP_STATUS.DEFAULT.getCode(), "", "", message);
    }

    /**
     * 400 잘못된 요청 오류
     * 
     * - 오류가 발생했을때,
     * Http Status Code : 400 ( Bad Request ),
     * field : "",
     * title : "",
     * 
     * @param errorCode
     * @param message
     * @return
     */
    public static ResponseEntity<?> error(Integer errorCode, String message) {
        return error(CUSTOMS_HTTP_STATUS.getTypeByCustomCode(errorCode), message);
    }

    public static ResponseEntity<?> error(CUSTOMS_HTTP_STATUS errorType, String message) {
        return error(errorType.getValue(), errorType.getCode(), "", errorType.getTitle(), message);
    }

    /**
     * 400 잘못된 요청 오류
     * 
     * - 오류가 발생했을때,
     * Http Status Code : 400 ( Bad Request ),
     * field : "",
     * 
     * @param errorCode
     * @param title
     * @param message
     * @return
     */
    public static ResponseEntity<?> error(Integer errorCode, String title, String message) {
        return error(CUSTOMS_HTTP_STATUS.getTypeByCustomCode(errorCode).getValue(), errorCode, "", title, message);
    }

    /**
     * 400 잘못된 요청 오류
     * 
     * - 오류가 발생했을때,
     * Http Status Code : 400 ( Bad Request ),
     * 
     * @param errorCode
     * @param field
     * @param title
     * @param message
     * @return
     */
    public static ResponseEntity<?> error(Integer errorCode, String field, String title, String message) {
        return error(CUSTOMS_HTTP_STATUS.getTypeByCustomCode(errorCode).getValue(), errorCode, field, title, message);
    }

    /**
     * 400 잘못된 요청 오류
     * 
     * - 오류가 발생했을때,
     * Http Status Code : 400 ( Bad Request ),
     * Custom Error Code : 9999,
     * 
     * @param field
     * @param title
     * @param message
     * @return
     */
    public static ResponseEntity<?> error(String field, String title, String message) {
        return error(CUSTOMS_HTTP_STATUS.DEFAULT.getValue(), CUSTOMS_HTTP_STATUS.DEFAULT.getCode(), field, title, message);
    }

    /**
     * 400 잘못된 요청 오류 또는 500 서버 내부 오류 등등 기타 원하는 HTTP 상태오류코드
     * 
     * - 오류가 발생했을때,
     * 
     * @param httpStatus
     * @param errorCode
     * @param field
     * @param title
     * @param message
     * @return
     */
    public static ResponseEntity<?> error(final Integer httpStatus, final Integer errorCode, final String field, final String title, final String message) {
        return errors(httpStatus, new ArrayList<Error>() {
            {
                add(new Error(errorCode, field, title, message));
            }
        });
    }

    /**
     * 오류가 발생했을때,
     * Http Status Code : 400
     * 
     * @param arrayList
     * @return
     */
    public static ResponseEntity<?> errors(List<Error> errors) {
        if (errors.get(0).getCode() != null) {
            return errors(errors.get(0).getCode(), errors);
        }
        return errors(CUSTOMS_HTTP_STATUS.DEFAULT.getValue(), errors);
    }

    /**
     * 400 잘못된 요청 오류
     * 
     * - 오류가 발생했을때,
     * 
     * @param httpStatus
     * @param errors
     * @return
     */
    public static ResponseEntity<?> errors(final Integer httpStatus, List<Error> errors) {
        Map<String, Object> resp = new HashMap<String, Object>();
        resp.put("errors", errors);
        return ResponseEntity.status(httpStatus).body(resp);
    }

    public static ResponseEntity<?> errors(HttpStatus httpStatus, List<Error> errors) {
        Map<String, Object> resp = new HashMap<String, Object>();
        resp.put("errors", errors);
        return ResponseEntity.status(httpStatus).body(resp);
    }

    public static ResponseEntity<?> errorWithEntity(Integer httpStatus, Object entity) {
        return ResponseEntity.status(httpStatus).body(entity);
    }

    public static ResponseEntity<?> errorWithEntity(HttpStatus httpStatus, Object entity) {
        return ResponseEntity.status(httpStatus).body(entity);
    }

    /**
     * 405 오류, Method Not Allowed
     * 
     * @return
     */
    public static ResponseEntity<?> methodNotAllowed() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    //
    // ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
    // ※※※※※※※ SUCCESS 관련 메소드 ※※※※※※※※※※※※
    // ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
    //
    /**
     * 200 정상
     * - 응답으로 아무것도 반환하지 않음.
     * 
     * @return
     */
    public static ResponseEntity<?> ok() {
        return ResponseEntity.ok().build();
    }

    /**
     * 200 정상
     * - 응답으로 반환하고자 하는 vo 객체( 객체유형 그대로 json을 제공하고 싶을경우 )
     * 
     * @param entity
     * @return
     */
    public static ResponseEntity<?> ok(Object entity) {
        return ResponseEntity.ok(entity);
    }

    public static ResponseEntity<?> ok(Object entity, MediaType contentType) {
        BodyBuilder builder = ResponseEntity.ok();
        builder.contentType(contentType);
        return builder.body(entity);
    }

    /**
     * 200 정상
     * - 응답으로 반환하고자 하는 Pages 객체( HATEOS 스타일의 포맷 )
     * 
     * @param pages
     * @return
     */
    public static <T> ResponseEntity<?> pages(Page<T> pages) {
        return ResponseEntity.ok(makePageEntityModel(pages, null, null));
    }

    /**
     * 200 정상
     * - 응답으로 반환하고자 하는 Pages 객체( HATEOS 스타일의 포맷 )
     * 
     * @param pages
     * @param errors
     * @return
     */
    public static <T> ResponseEntity<?> pages(Page<T> pages, Error errors) {
        return ResponseEntity.ok(makePageEntityModel(pages, errors, null));
    }

    /**
     * 200 정상
     * - 응답으로 반환하고자 하는 Pages 객체( HATEOS 스타일의 포맷 )
     * 
     * @param pages
     * @param ResourceAssembler
     * @return
     */
    public static <T> ResponseEntity<?> pages(Page<T> pages, PersistentEntityResourceAssembler resourceAssembler) {
        return ResponseEntity.ok(makePageEntityModel(pages, null, resourceAssembler));
    }

    /**
     * 200 정상
     * - 응답으로 반환하고자 하는 Pages 객체( HATEOS 스타일의 포맷 )
     * 
     * @param pages
     * @param errors
     * @param ResourceAssembler
     * @return
     */
    public static <T> ResponseEntity<?> pages(Page<T> pages, Error errors, PersistentEntityResourceAssembler resourceAssembler) {
        return ResponseEntity.ok(makePageEntityModel(pages, errors, resourceAssembler));
    }

    /**
     * 200 정상
     * - 응답으로 반환하고자 하는 Entity 객체( HATEOS 스타일의 포맷 )
     * 
     * @param obj
     * @return
     */
    public static <T> ResponseEntity<?> entity(T obj) {
        return ResponseEntity.ok(makeEntityModel(obj, null));
    }

    /**
     * 200 정상
     * - 응답으로 반환하고자 하는 Entity 객체( HATEOS 스타일의 포맷 )
     * 
     * @param obj
     * @param resourceAssembler
     * @return
     */
    public static <T> ResponseEntity<?> entity(T obj, PersistentEntityResourceAssembler resourceAssembler) {
        return ResponseEntity.ok(makeEntityModel(obj, resourceAssembler));
    }

    /**
     * 200 정상
     * - 응답으로 반환하고자 하는 Entity를 내장한 List형 객체( HATEOS 스타일의 포맷 )
     * 
     * @param lists
     * @return
     */
    public static <T> ResponseEntity<?> list(List<T> lists) {
        return ResponseEntity.ok(makeListEntityModel(lists, null));
    }

    /**
     * 200 정상
     * - 응답으로 반환하고자 하는 Entity를 내장한 List형 객체( HATEOS 스타일의 포맷 )
     * 
     * @param lists
     * @param resourceAssembler
     * @return
     */
    public static <T> ResponseEntity<?> list(List<T> lists, PersistentEntityResourceAssembler resourceAssembler) {
        return ResponseEntity.ok(makeListEntityModel(lists, resourceAssembler));
    }

    /**
     * 204 정상 (단, DELETE 요청시 사용해야함)
     * - 삭제시, No Content 상태코드반환
     * 
     * @return
     */
    public static ResponseEntity<?> noContent() {
        return ResponseEntity.noContent().build();
    }

    /**
     * 202 정상
     * - Accepted.
     * 
     */
    public static ResponseEntity<?> accepted() {
        return ResponseEntity.accepted().build();
    }

    /**
     * 401, 권한 없음
     * - Unauthorized
     * 
     * @return
     */
    public static ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    /**
     * 401, 권한 없음
     * - Unauthorized인데 Body가 필요할경우
     * 
     * @return
     */
    public static ResponseEntity<?> unauthorized(Object entity) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(entity);
    }

    /**
     * 
     * @param errorCode
     * @param message
     * @return
     */
    public static ResponseEntity<?> unauthorized(Integer errorCode, String message) {
        return errors(HttpStatus.UNAUTHORIZED, new ArrayList<Error>() {
            {
                add(new Error(errorCode, "", "", message));
            }
        });
    }

    /**
     * 
     * @param entity
     * @return
     */
    public static ResponseEntity<?> accessDenied(Object entity) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(entity);
    }
    //
    // ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
    // ※※※※※※※ Custom Method ※※※※※※※※※※※※
    // ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
    //

    public static <T> ResponseEntity<?> emptyPages(Pageable pageable) {
        Link baseLink = Link.of(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString());
        PageMetadata pageMetadata = new PageMetadata(0, 0, 0, 0);
        List<EntityModel<?>> entityModels = new ArrayList<EntityModel<?>>();
        PagedModel<?> pagedModel = PagedModel.of(entityModels, pageMetadata, baseLink);
        return ResponseEntity.ok(pagedModel);
    }

    public static <T> ResponseEntity<?> addContentToPage(Page<T> pages, String contentName, Map<String, Object> addContent, PersistentEntityResourceAssembler resourceAssembler) {
        Link baseLink = Link.of(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString());
        PageMetadata pageMetadata = new PageMetadata(pages.getSize(), pages.getNumber(), pages.getTotalElements(), pages.getTotalPages());
        List<EntityModel<?>> entityModels = new ArrayList<EntityModel<?>>();
        for (T page : pages) {
            if (resourceAssembler == null) {
                entityModels.add(EntityModel.of(page, Link.of("/")));
            } else {
                entityModels.add(page == null ? null : resourceAssembler.toModel(page));
            }
        }
        addContent.put(contentName, entityModels);
        List<Map<String, Object>> content = new ArrayList<>();
        content.add(addContent);
        PagedModel<?> pagedModel = PagedModel.of(content, pageMetadata, baseLink);
        return ResponseEntity.ok(pagedModel);
    }

    //
    // ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
    // ※※※※※※※ 기존 CollectionModel 생성 유틸 메소드 ※※※※※※※※※※※※
    // ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
    //
    private static <T> PagedModel<EntityModel<?>> makePageEntityModel(Page<T> pages) {
        return makePageEntityModel(pages, null, null);
    }

    private static <T> PagedModel<EntityModel<?>> makePageEntityModel(Page<T> pages, Error errors) {
        return makePageEntityModel(pages, errors, null);
    }

    private static <T> PagedModel<EntityModel<?>> makePageEntityModel(Page<T> pages, PersistentEntityResourceAssembler resourceAssembler) {
        return makePageEntityModel(pages, null, resourceAssembler);
    }

    private static <T> PagedModel<EntityModel<?>> makePageEntityModel(Page<T> pages, Error errors, PersistentEntityResourceAssembler resourceAssembler) {
        Link baseLink = Link.of(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString());
        PageMetadata pageMetadata = new PageMetadata(pages.getSize(), pages.getNumber(), pages.getTotalElements(), pages.getTotalPages());
        List<EntityModel<?>> entityModels = new ArrayList<EntityModel<?>>();
        for (T page : pages) {
            if (resourceAssembler == null) {
                entityModels.add(EntityModel.of(page, Link.of("/")));
            } else {
                entityModels.add(page == null ? null : resourceAssembler.toModel(page));
            }
        }
        PagedModel<EntityModel<?>> pagedModel = PagedModel.of(entityModels, pageMetadata, baseLink);
        return pagedModel;
    }

    private static <T> EntityModel<?> makeEntityModel(T obj) {
        return makeEntityModel(obj, null);
    }

    private static <T> EntityModel<?> makeEntityModel(T obj, PersistentEntityResourceAssembler resourceAssembler) {
        Link baseLink = Link.of(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString());
        EntityModel<?> entityModel = null;
        if (obj == null) {
            return null;
        }
        if (resourceAssembler == null) {
            entityModel = EntityModel.of(obj, baseLink);
        } else {
            entityModel = resourceAssembler.toModel(obj);
        }
        return entityModel;
    }

    private static <T> CollectionModel<EntityModel<?>> makeListEntityModel(List<T> lists) {
        return makeListEntityModel(lists, null);
    }

    private static <T> CollectionModel<EntityModel<?>> makeListEntityModel(List<T> lists, PersistentEntityResourceAssembler resourceAssembler) {
        Link baseLink = Link.of(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString());
        List<EntityModel<?>> entityModels = new ArrayList<EntityModel<?>>();
        for (T list : lists) {
            if (resourceAssembler == null) {
                entityModels.add(EntityModel.of(list, Link.of("/")));
            } else {
                entityModels.add(list == null ? null : resourceAssembler.toModel(list));
            }
        }
        CollectionModel<EntityModel<?>> collectionModel = CollectionModel.of(entityModels, baseLink);
        return collectionModel;
    }

    public static <T> ResponseEntity<?> json(RestApiVO result) {
        int status = result.getStatus();
        switch (status) {
            case 200:
                return jsonString(result.getResultJson());
            case 202:
                return ResponseEntity.accepted().build();
            case 204:
                return noContent();
            case 401:
                return unauthorized();
            case 999:
                return ResponseEntity.status(400).body(result.getResultJson());
            default:
                return ResponseEntity.status(status).body(result.getResultJson());
        }
    }

    /**
     * 200 정상
     * - 응답으로 반환하고자 하는 JsonString
     * 
     * @param obj
     * @return
     */
    public static <T> ResponseEntity<?> jsonString(String jsonString) {
        return ResponseEntity.ok(jsonString);
    }
    
    @Getter
    public enum CUSTOMS_HTTP_STATUS {
        OK(200, 200, "성공"),
        NO_CONTENT(204, 204, "콘텐츠 없음"),
        
        BAD_REQUEST(400, 400, "잘못된 요청"),
        DUPLICATION_CHECK_FAILED(400, 400,"중복체크 실패"),
        UNAUTHORIZED(401, 401, "권한 없음"),
        FORBIDDEN(403, 403, "찾을 수 없음"),
        NOT_FOUND(404, 404, "찾을 수 없음"),
        NOT_ALLOWED_REQUEST(406, 406, "허용되지 않음"),
        PRECONDITION_FAILED(412, 412, "사전조건 실패"),
        
        INTERNAL_SERVER_ERROR(500, 500,"서버내 오류 발생"), // 서버측의 오동작에 의한 오류
        CLIENT_ERROR(400, 9999,"클라이언트에 의한 잘못된 요청"), // 클라이언트에 의해 발생한 오류
        UNKNOWN_ERROR(999, 9999,"알려지지 않은 오류발생"), // 알려지지 않은 오류발생
        DEFAULT(400, 9999,"알려지지 않은 오류발생"); // 알려지지 않은 오류발생
        
        
        private Integer value; // http status 코드 사용권장( 200, 400, 500 등등)
        private Integer code; // 시스템에 특화된 UNIQUE한 에러코드 정의
        private String title; // 오류코드에 대한 간략한 설명(popup류 화면의 제목)
        
        private CUSTOMS_HTTP_STATUS(Integer value, Integer code, String title){
            this.value = value;
            this.code = code;
            this.title = title;
        }
        
        public int getStatus() {
            return HttpStatus.valueOf(value).value();
        }
        
        public HttpStatus getHttpStatus() {
            return HttpStatus.valueOf(value);
        }
        
        public static HttpStatus getHttpStatus(CUSTOMS_HTTP_STATUS customHttpStatus) {
            return HttpStatus.valueOf(customHttpStatus.value);
        }
        
        
        public static CUSTOMS_HTTP_STATUS getTypeByCustomCode(Integer customCode) {
            if (customCode != null) {
                for (CUSTOMS_HTTP_STATUS status : CUSTOMS_HTTP_STATUS.values()) {
                    if (status.getCode().equals(customCode)){
                        return status;
                    }
                }
            }
            return UNKNOWN_ERROR;
        }
        
        public static CUSTOMS_HTTP_STATUS getTypeByCustomCode(String customCode) {
            return getTypeByCustomCode(Integer.valueOf(customCode));
        }
    }
    
}
