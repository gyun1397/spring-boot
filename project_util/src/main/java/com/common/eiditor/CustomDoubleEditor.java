package com.common.eiditor;

import java.beans.PropertyEditorSupport;

public class CustomDoubleEditor extends PropertyEditorSupport {
    
    private final boolean emptyAsNull;
    
    public CustomDoubleEditor(boolean emptyAsNull) {
        this.emptyAsNull = emptyAsNull;
    }
    

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        try {
            if (this.emptyAsNull && text == null) {
                setValue(false);
            }
            else {
                Double value = Double.valueOf(text.trim());
                setValue(value);
            }
        } catch (Exception e) {
            setValue(0);
        }
    }
    
    
    @Override
    public String getAsText() {
        Object value = getValue();
        return (value != null ? value.toString() : "");
    }
    
}
