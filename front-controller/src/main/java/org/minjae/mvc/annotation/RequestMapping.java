package org.minjae.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.minjae.mvc.controller.RequestMethod;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc.annotation
 * @fileName : RequestMapping
 * @date : 2025-03-31
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value() default "";

    RequestMethod[] method() default {};
}
