package com.common.exception;

import com.common.error.Error;
import com.common.util.ResponseEntityUtil.CUSTOMS_HTTP_STATUS;
import lombok.Data;

@Data
public class BadRequestException extends CustomDefaultException {
    private final static CUSTOMS_HTTP_STATUS DEFAULT_STATUS = CUSTOMS_HTTP_STATUS.BAD_REQUEST;
    private final static String DEFAULT_TITLE = DEFAULT_STATUS.getTitle();
    private final static String DEFAULT_MESSAGE = "잘못된 API를 요청하였습니다.";
    
    public BadRequestException() {
        this(DEFAULT_MESSAGE);
    }
    
    public BadRequestException(String detailMessage) {
        this(null, detailMessage);
    }
    
    public BadRequestException(String field, String detailMessage) {
        super(DEFAULT_STATUS);
        this.errors.add(new Error(DEFAULT_STATUS.getCode(), field, DEFAULT_TITLE, detailMessage));
    }
    
    public BadRequestException(Object object, String field, String detailMessage) {
        this(field, detailMessage);
        this.object = object;
    }

    
}