package org.minjae.mvc;

import java.util.HashMap;
import java.util.Map;
import org.minjae.mvc.controller.Controller;
import org.minjae.mvc.controller.HomeController;

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
    private Map<String, Controller> mappings = new HashMap<String, Controller>();

    void init(){
        mappings.put("/", new HomeController());
    }

    public Controller findHandler(String urlPath){
        return mappings.get(urlPath);
    }
}
