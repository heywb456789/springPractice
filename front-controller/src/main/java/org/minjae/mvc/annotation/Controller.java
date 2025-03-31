package org.minjae.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc.annotation
 * @fileName : Controller
 * @date : 2025-03-31
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {

}
