package org.minjae.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.annotation
 * @fileName : Inject
 * @date : 2025-03-31
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Target({
    ElementType.CONSTRUCTOR,
    ElementType.FIELD,
    ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {

}
