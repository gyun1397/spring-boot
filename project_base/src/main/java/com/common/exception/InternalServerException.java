package com.common.exception;

import com.common.error.Error;
import com.common.util.ResponseEntityUtil.CUSTOMS_HTTP_STATUS;

public class InternalServerException  extends CustomDefaultException {

    private final static CUSTOMS_HTTP_STATUS DEFAULT_STATUS = CUSTOMS_HTTP_STATUS.INTERNAL_SERVER_ERROR;
    private final static String DEFAULT_TITLE = "시스템 오류 발생";
    private final static String DEFAULT_MESSAGE = "서버 내부에 오류가 발생하였습니다.";
    
    public InternalServerException() {
        this(DEFAULT_MESSAGE);
    }
    
    public InternalServerException(String detailMessage) {
        this(null, detailMessage);
    }
    
    public InternalServerException(String field, String detailMessage) {
        super(DEFAULT_STATUS);
        this.errors.add(new Error(DEFAULT_STATUS.getCode(), field, DEFAULT_TITLE, detailMessage));
    }

}
