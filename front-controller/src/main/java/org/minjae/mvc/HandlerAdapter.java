package org.minjae.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.minjae.mvc.view.ModelAndView;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc
 * @fileName : HandlerAdapter
 * @date : 2025-03-07
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface HandlerAdapter {

    boolean supports(Object handler);

    ModelAndView handle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception;

}
