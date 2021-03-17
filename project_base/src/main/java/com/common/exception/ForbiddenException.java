package com.common.exception;

import com.common.error.Error;
import com.common.util.ResponseEntityUtil.CUSTOMS_HTTP_STATUS;
import lombok.Data;

@Data
public class ForbiddenException extends CustomDefaultException {
    
    private final static CUSTOMS_HTTP_STATUS DEFAULT_STATUS = CUSTOMS_HTTP_STATUS.FORBIDDEN;
    private final static String DEFAULT_TITLE = "금지된 요청";
    private final static String DEFAULT_MESSAGE = "요청이 금지/거부 되었습니다.";
    
    public ForbiddenException() {
        this(DEFAULT_MESSAGE);
    }
    
    public ForbiddenException(String detailMessage) {
        this(null, detailMessage);
    }
    
    public ForbiddenException(String field, String detailMessage) {
        super(DEFAULT_STATUS);
        this.errors.add(new Error(DEFAULT_STATUS.getCode(), field, DEFAULT_TITLE, detailMessage));
    }
    

    public ForbiddenException(Object object, String field, String detailMessage) {
        this(field, detailMessage);
        this.object = object;
    }
}