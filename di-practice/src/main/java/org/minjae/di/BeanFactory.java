package org.minjae.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.minjae.annotation.Inject;
import org.minjae.controller.UserController;
import org.reflections.ReflectionUtils;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.di
 * @fileName : BeanFactory
 * @date : 2025-03-31
 * @description :
 * @AUTHOR : MinjaeKim
 */
public class BeanFactory {

    private final Set<Class<?>> preIntiatialClazz;
    private Map<Class<?>, Object> beans = new HashMap<>();

    public BeanFactory(Set<Class<?>> preIntiatialClazz) {
        this.preIntiatialClazz = preIntiatialClazz;
        initialize();
    }

    private void initialize() {
        for (Class<?> clazz : preIntiatialClazz) {
            Object instance = createInstance(clazz);
            beans.put(clazz, instance);
        }
    }

    private Object createInstance(Class<?> clazz) {
        //생성자
        Constructor<?> constructors = findConstructor(clazz);

        //파라미터
        List<Object> parameters = new ArrayList<>();
        for(Class<?> typeClass : constructors.getParameterTypes()) {
            parameters.add(getParameterByClass(typeClass));
        }

        //인스턴스 생성
        try {
            return constructors.newInstance(parameters.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getParameterByClass(Class<?> typeClass) {
        Object instance = getBean(typeClass);

        if(Objects.nonNull(instance)) {
            return instance;
        }

        return createInstance(typeClass);
    }

    private Constructor<?> findConstructor(Class<?> clazz) {
        //Inject가 붙은 클래스 타입에서 모두 가져온다.
        Constructor<?> constructor = BeanFactoryUtils.getInjectedConstructor(clazz);

        if(Objects.nonNull(constructor)){
            return constructor;
        }
        return clazz.getConstructors()[0];
//        Set<Constructor> injectedConstructor = ReflectionUtils.getAllConstructors(clazz,
//            ReflectionUtils.withAnnotation(Inject.class));
//        if(injectedConstructor.isEmpty()) {
//            return null;
//        }
//        return injectedConstructor.iterator().next();
    }

    public <T> T getBean(Class<T> requiredType) {
        return (T) beans.get(requiredType);
    }
}
