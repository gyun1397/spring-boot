package com.common.eiditor;

import java.beans.PropertyEditorSupport;
import com.common.util.DataUtil;

public class CustomBooleanEditor extends PropertyEditorSupport {
    
    private final boolean emptyAsNull;
    
    public CustomBooleanEditor(boolean emptyAsNull) {
        this.emptyAsNull = emptyAsNull;
    }
    

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            if (this.emptyAsNull && text == null) {
                setValue(false);
            }
            else {
                Boolean value = DataUtil.isTrue(text.trim());
                setValue(value);
            }
        } catch (Exception e) {
            setValue(false);
        }
    }
    
    
    @Override
    public String getAsText() {
        Object value = getValue();
        return (value != null ? value.toString() : "");
    }
    
}
