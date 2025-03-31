package org.minjae.mvc;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc
 * @fileName : AnnotationHandler
 * @date : 2025-03-31
 * @description :
 * @AUTHOR : MinjaeKim
 */
public class AnnotationHandler {


    private final Class<?> clazz;
    private final Method targetMethod;

    public AnnotationHandler(Class<?> clazz, Method targetMethod) {
        this.clazz = clazz;
        this.targetMethod = targetMethod;

    }

    public String handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Constructor<?> declaredConstructor = clazz.getDeclaredConstructor();
        Object instance = declaredConstructor.newInstance();

        return String.valueOf(targetMethod.invoke(instance,request, response));
    }
}
