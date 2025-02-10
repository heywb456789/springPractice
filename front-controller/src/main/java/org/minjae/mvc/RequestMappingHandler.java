package org.minjae.mvc;

import java.util.HashMap;
import java.util.Map;
import org.minjae.mvc.controller.Controller;
import org.minjae.mvc.controller.HandlerKey;
import org.minjae.mvc.controller.HomeController;
import org.minjae.mvc.controller.RequestMethod;
import org.minjae.mvc.controller.UserCreateController;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc
 * @fileName : RequestMappingHandler
 * @date : 2025-02-06
 * @description : ===========================================================
 * @DATE @AUTHOR       @NOTE ----------------------------------------------------------- 2025-02-06
 * MinjaeKim       최초 생성
 */
public class RequestMappingHandler {
    //[key] /users  [value] UserController
//    private Map<String, Controller> mappings = new HashMap<String, Controller>();
    private Map<HandlerKey, Controller> mappings = new HashMap<HandlerKey, Controller>();

    void init(){
        mappings.put(new HandlerKey(RequestMethod.GET,"/"), new HomeController());
        mappings.put(new HandlerKey(RequestMethod.GET,"/users"), new HomeController());
        mappings.put(new HandlerKey(RequestMethod.POST,"/users"), new UserCreateController());
    }

    public Controller findHandler(HandlerKey uriPath){
        return mappings.get(uriPath);
    }
}
