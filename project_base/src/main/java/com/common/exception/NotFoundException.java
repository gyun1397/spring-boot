package com.common.exception;

import com.common.error.Error;
import com.common.util.ResponseEntityUtil.CUSTOMS_HTTP_STATUS;
import lombok.Data;

@Data
public class NotFoundException extends CustomDefaultException {
    private final static CUSTOMS_HTTP_STATUS DEFAULT_STATUS = CUSTOMS_HTTP_STATUS.NOT_FOUND;
    private final static String DEFAULT_TITLE = "찾을수 없음";
    private final static String DEFAULT_MESSAGE = "요청하신 정보가 존재하지 않습니다.";
    
    public NotFoundException() {
        this(DEFAULT_MESSAGE);
    }
    
    public NotFoundException(String detailMessage) {
        this(null, detailMessage);
    }
    
    public NotFoundException(String field, String detailMessage) {
        super(DEFAULT_STATUS);
        this.errors.add(new Error(DEFAULT_STATUS.getCode(), field, DEFAULT_TITLE, detailMessage));
    }
    
    public NotFoundException(Object object, String field, String detailMessage) {
        this(field, detailMessage);
        this.object = object;
    }
}