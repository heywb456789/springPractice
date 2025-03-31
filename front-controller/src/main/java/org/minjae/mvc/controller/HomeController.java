package org.minjae.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.minjae.mvc.annotation.Controller;
import org.minjae.mvc.annotation.RequestMapping;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc.controller
 * @fileName : HomeController
 * @date : 2025-02-06
 * @description : ===========================================================
 * @DATE @AUTHOR       @NOTE ----------------------------------------------------------- 2025-02-06
 * MinjaeKim       최초 생성
 */
//public class HomeController implements Controller {
@Controller
public class HomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String handleRequest(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return "home";
    }
}
