package org.minjae.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.minjae.mvc.controller.Controller;
import org.minjae.mvc.view.ModelAndView;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc
 * @fileName : SimpleControllerHandlerAdapter
 * @date : 2025-03-07
 * @description :
 * @AUTHOR : MinjaeKim
 */
public class SimpleControllerHandlerAdapter implements HandlerAdapter{

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof Controller);
    }

    @Override
    public ModelAndView handle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        String viewName = ((Controller) handler).handleRequest(req, resp);
        return new ModelAndView(viewName);
    }
}
