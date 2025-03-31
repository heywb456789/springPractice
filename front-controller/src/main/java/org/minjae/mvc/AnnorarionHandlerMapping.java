package org.minjae.mvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.minjae.mvc.annotation.Controller;
import org.minjae.mvc.annotation.RequestMapping;
import org.minjae.mvc.controller.HandlerKey;
import org.minjae.mvc.controller.RequestMethod;
import org.reflections.Reflections;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc
 * @fileName : AnnorarionHandlerMapping
 * @date : 2025-03-31
 * @description :
 * @AUTHOR : MinjaeKim
 */
public class AnnorarionHandlerMapping implements HandlerMapping{

    private final Object[] basePackage;

    private Map<HandlerKey, AnnotationHandler> handlers = new HashMap<>();

    public AnnorarionHandlerMapping(Object... basePackage) {
        this.basePackage = basePackage;
    }

    public void initialize(){
        //reflection으로
        Reflections reflections = new Reflections(basePackage);
        //homeController Class Type 객체만 @Controller 붙여서 이거만 넘어옴
        Set<Class<?>> clazzesWithControllerAnnotaion= reflections.getTypesAnnotatedWith(Controller.class);
        clazzesWithControllerAnnotaion.forEach(clazz ->
                Arrays.stream(clazz.getDeclaredMethods()).forEach(declaredMethod ->{
                    RequestMapping requestMapping = declaredMethod.getDeclaredAnnotation(RequestMapping.class);

                    Arrays.stream(getRequestMethod(requestMapping))
                        .forEach(requestMethod -> handlers.put(
                            new HandlerKey(requestMethod, requestMapping.value()),new AnnotationHandler(clazz, declaredMethod)
                        ));
                })
            );
    }

    private RequestMethod[] getRequestMethod(RequestMapping requestMapping) {
        return requestMapping.method();
    }

    @Override
    public Object findHandler(HandlerKey handlerKey) {
        return handlers.get(handlerKey);
    }
}
