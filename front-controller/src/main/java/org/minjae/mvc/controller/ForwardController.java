package org.minjae.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc.controller
 * @fileName : ForwardController
 * @date : 2025-03-07
 * @description :
 * @AUTHOR : MinjaeKim
 */
public class ForwardController implements Controller {

    public ForwardController(String forwardUri) {
        this.forwardUri = forwardUri;
    }

    private final String forwardUri;

    @Override
    public String handleRequest(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        return forwardUri;
    }
}
