package org.minjae.di;

import java.lang.reflect.Constructor;
import java.util.Set;
import org.minjae.annotation.Inject;
import org.reflections.ReflectionUtils;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.di
 * @fileName : BeanFactoryUtils
 * @date : 2025-03-31
 * @description :
 * @AUTHOR : MinjaeKim
 */
public class BeanFactoryUtils {

    public static Constructor<?> getInjectedConstructor(Class<?> clazz) {
        Set<Constructor> injectedConstructor = ReflectionUtils.getAllConstructors(clazz,
            ReflectionUtils.withAnnotation(Inject.class));
        if(injectedConstructor.isEmpty()) {
            return null;
        }
        return injectedConstructor.iterator().next();
    }

}
