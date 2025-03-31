package org.minjae.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.minjae.mvc.view.ModelAndView;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc
 * @fileName : AnnotationHandlerAdapter
 * @date : 2025-03-31
 * @description :
 * @AUTHOR : MinjaeKim
 */
public class AnnotationHandlerAdapter implements HandlerAdapter{

    @Override
    public boolean supports(Object handler) {
        return handler instanceof ModelAndView;
    }

    @Override
    public ModelAndView handle(HttpServletRequest req, HttpServletResponse resp, Object handler)
        throws Exception {
        String viewName = ((AnnotationHandler) handler).handle(req, resp);
        return new ModelAndView(viewName);
    }
}
