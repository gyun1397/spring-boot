package com.common.error;

import lombok.Data;

@Data
public class Error {
    private final static int DEFAULT_CODE = 400;
    private final static String DEFAULT_TITLE = "잘못된 API";
    private final static String DEFAULT_MESSAGE = "잘못된 API 입니다.";
    private Integer code;
    private String  field;
    private String  title;
    private Object  detail;

    public Error() {
        this(DEFAULT_CODE, "", DEFAULT_TITLE, DEFAULT_MESSAGE);
    };

    public Error(String field, Object message) {
        this(DEFAULT_CODE, field, DEFAULT_TITLE, message);
    }
    
    public Error(String field, String title, Object message) {
        this(DEFAULT_CODE, field, title, message);
    }

    public Error(Integer code, String field, String title, Object message) {
        super();
        this.code = code;
        this.field = field;
        this.title = title;
        this.detail = message;
    }

    public Error(String message) {
        this(DEFAULT_CODE, "", DEFAULT_TITLE, message);
    }

    public Error(int errorCode, String message) {
        this(errorCode, "", DEFAULT_TITLE, message);
    }

    public Error(int errorCode, String title, String message) {
        this(errorCode, "", title, message);
    }

    public Error addCode(Integer code) {
        this.code = code;
        return this;
    }

    public Error addField(String field) {
        this.field = field;
        return this;
    }

    public Error addTitle(String title) {
        this.title = title;
        return this;
    }

    public Error addDetail(String detail) {
        this.detail = detail;
        return this;
    }
}
