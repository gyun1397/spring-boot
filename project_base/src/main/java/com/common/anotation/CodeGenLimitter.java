package com.common.anotation;

import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CodeGenLimitter {
    Limitter value() default Limitter.ALL;
    
    public enum Limitter {
        NONE, CONTROLLER, SERVICE, REPOSITORY, ALL
    }

}
