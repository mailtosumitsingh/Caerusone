package org.ptg.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CollTypeAnot {
	String collType() default "Single";
	String collValType() default "java.lang.String";
}
