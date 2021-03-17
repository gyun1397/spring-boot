package com.common.exception;

import com.common.error.Error;
import com.common.util.ResponseEntityUtil.CUSTOMS_HTTP_STATUS;
import lombok.Data;

@Data
public class UnauthorizedException extends CustomDefaultException {
    private final static CUSTOMS_HTTP_STATUS DEFAULT_STATUS = CUSTOMS_HTTP_STATUS.UNAUTHORIZED;
    private final static String DEFAULT_TITLE = "권한 없음";
    private final static String DEFAULT_MESSAGE = "접근 권한이 없습니다.";
    
    public UnauthorizedException() {
        this(DEFAULT_MESSAGE);
    }
    
    public UnauthorizedException(String detailMessage) {
        this(null, detailMessage);
    }
    
    public UnauthorizedException(String field, String detailMessage) {
        super(DEFAULT_STATUS);
        this.errors.add(new Error(DEFAULT_STATUS.getCode(), field, DEFAULT_TITLE, detailMessage));
    }
    
    public UnauthorizedException(Object object, String field, String detailMessage) {
        this(field, detailMessage);
        this.object = object;
    }
}
