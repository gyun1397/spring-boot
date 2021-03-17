package com.common.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.common.error.Error;
import com.common.util.ResponseEntityUtil.CUSTOMS_HTTP_STATUS;
import lombok.Data;

@Data
public class CustomDefaultException extends RuntimeException {
    private final static CUSTOMS_HTTP_STATUS DEFAULT_STATUS = CUSTOMS_HTTP_STATUS.BAD_REQUEST;
    protected List<Error> errors;
    protected Object      object;
    protected CUSTOMS_HTTP_STATUS  status;

    public CustomDefaultException() {
        this.status = DEFAULT_STATUS;
    }

    public CustomDefaultException(CUSTOMS_HTTP_STATUS status) {
        this.errors = new ArrayList<>();
        this.status = status;
    }

    public CustomDefaultException(CUSTOMS_HTTP_STATUS status, List<Error> errors) {
        this.errors = errors;
        this.status = status;
    }

    public CustomDefaultException(List<Error> errors) {
        this.errors = errors;
    }

    public CustomDefaultException(String field, String detailMessage) {
        this.status = DEFAULT_STATUS;
        this.errors.add(new Error(DEFAULT_STATUS.getCode(), field, detailMessage));
    }

    public CustomDefaultException(String detailMessage) {
        this("", detailMessage);
    }

    public CUSTOMS_HTTP_STATUS getCUSTOMS_HTTP_STATUS() {
        return status;
    }

    public Object getErrorsObj() {
        Map<String, Object> resp = new HashMap<String, Object>();
        resp.put("errors", errors);
        return resp;
    }
}
