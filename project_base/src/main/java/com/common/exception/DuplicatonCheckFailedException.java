package com.common.exception;

import com.common.error.Error;
import com.common.util.ResponseEntityUtil.CUSTOMS_HTTP_STATUS;
import lombok.Data;

@Data
public class DuplicatonCheckFailedException extends CustomDefaultException {
    private final static CUSTOMS_HTTP_STATUS DEFAULT_STATUS = CUSTOMS_HTTP_STATUS.BAD_REQUEST;
    private final static String DEFAULT_TITLE = "중복체크 오류";
    private final static String DEFAULT_MESSAGE = "이미 등록 되어 있는 정보 입니다.";
    
    public DuplicatonCheckFailedException() {
        this(DEFAULT_MESSAGE);
    }
    
    public DuplicatonCheckFailedException(String detailMessage) {
        this(null, detailMessage);
    }
    
    public DuplicatonCheckFailedException(String field, String detailMessage) {
        super(DEFAULT_STATUS);
        this.errors.add(new Error(DEFAULT_STATUS.getCode(), field, DEFAULT_TITLE, detailMessage));
    }

    public DuplicatonCheckFailedException(Object object, String field, String detailMessage) {
        this(field, detailMessage);
        this.object = object;
    }
    

}