package com.common.util;

public class RegularExpression {
    public final static String SPACE                           = "([\\s*])";
    public final static String HYPHEN                          = "([-])";
    public final static String NUM                             = "([0-9])";
    public final static String ENG                             = "([a-zA-Z])";
    public final static String KR                              = "([ㄱ-힣])";
    public final static String NUM_ENG                         = "([0-9a-zA-Z])";
    public final static String NUM_KR                          = "([0-9ㄱ-힣])";
    public final static String ENG_KR                          = "([a-zA-Zㄱ-힣])";
    public final static String NUM_ENG_KR                      = "([0-9a-zA-Zㄱ-힣])";
    public final static String INTEGER                         = "^(-?[0-9])+$";
    public final static String NUMERIC                         = "^(-?(?:[0-9]*\\.)?[0-9]+)+$";
    public final static String EMAIL                           = "^([\\w-\\.])+@([\\w-])+(\\.[a-zA-Z]{2,6}){1,2}$";
    
}