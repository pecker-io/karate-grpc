package com.github.thinkerou.testng;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.METHOD)
@Documented
public @interface FudgeClassPath {

}
