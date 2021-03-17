package com.common.exception;

import com.common.error.Error;
import com.common.util.ResponseEntityUtil.CUSTOMS_HTTP_STATUS;
import lombok.Data;

@Data
public class PreconditionException extends CustomDefaultException {
    private final static CUSTOMS_HTTP_STATUS DEFAULT_STATUS = CUSTOMS_HTTP_STATUS.PRECONDITION_FAILED;
    private final static String DEFAULT_TITLE = "사전조건 실패";
    private final static String DEFAULT_MESSAGE = "사전조건에 실패하였습니다.";
    
    public PreconditionException() {
        this(DEFAULT_MESSAGE);
    }
    
    public PreconditionException(String detailMessage) {
        this(null, detailMessage);
    }
    
    public PreconditionException(String field, String detailMessage) {
        super(DEFAULT_STATUS);
        this.errors.add(new Error(DEFAULT_STATUS.getCode(), field, DEFAULT_TITLE, detailMessage));
    }
    
    public PreconditionException(Object object, String field, String detailMessage) {
        this(field, detailMessage);
        this.object = object;
    }
}
