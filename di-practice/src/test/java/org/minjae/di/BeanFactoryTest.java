package org.minjae.di;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.minjae.annotation.Controller;
import org.minjae.annotation.Service;
import org.minjae.controller.UserController;
import org.reflections.Reflections;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.di
 * @fileName : BeanFactoryTest
 * @date : 2025-03-31
 * @description :
 * @AUTHOR : MinjaeKim
 */
class BeanFactoryTest {
    private Reflections reflections;
    private BeanFactory beanFactory;

    @BeforeEach
    void setUp() {
        reflections = new Reflections("org.minjae");
        Set<Class<?>> preIntiatialClazz = getTypesAnnotaedWith(Controller.class, Service.class);
        beanFactory = new BeanFactory(preIntiatialClazz);
    }

    //Reflections로 controller Annotations 타입이 붙은 클래스 타입 객체를 찾아서 beans에 add
    private Set<Class<?>> getTypesAnnotaedWith(Class<? extends Annotation>... annotations) {
        Set<Class<?>> beans = new HashSet<>();
        for(Class<? extends Annotation> annotation : annotations) {
            beans.addAll(reflections.getTypesAnnotatedWith(annotation));
        }
        return beans;
    }

    @Test
    void test() {
        UserController userController = beanFactory.getBean(UserController.class);

        assertThat(userController).isNotNull();
        assertThat(userController.getUserService()).isNotNull();
    }

}