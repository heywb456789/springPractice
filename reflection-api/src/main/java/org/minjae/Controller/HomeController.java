package org.minjae.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.minjae.annotaion.Controller;
import org.minjae.annotaion.RequestMapping;
import org.minjae.annotaion.RequestMethod;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.Controller
 * @fileName : HomeController
 * @date : 2024-10-22
 * @description : ===========================================================
 * @DATE @AUTHOR       @NOTE ----------------------------------------------------------- 2024-10-22
 * MinjaeKim       최초 생성
 */

@Controller
public class HomeController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(HttpServletRequest request, HttpServletResponse response){
        return "home";

    }
}
