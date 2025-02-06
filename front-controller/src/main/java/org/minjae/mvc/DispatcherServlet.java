package org.minjae.mvc;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.minjae.mvc.controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc
 * @fileName : DispatcherServlet
 * @date : 2025-02-06
 * @description : ===========================================================
 * @DATE @AUTHOR       @NOTE ----------------------------------------------------------- 2025-02-06
 * MinjaeKim       최초 생성
 */
@WebServlet("/") //모든 경로에 대한 요청을 캐치
public class DispatcherServlet extends HttpServlet {

    private final static Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    private RequestMappingHandler requestMappingHandler;

    @Override
    public void init() throws ServletException {
        requestMappingHandler = new RequestMappingHandler();
        requestMappingHandler.init();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        log.info("Dispatching request...");

        try {
            Controller handler = requestMappingHandler.findHandler(request.getRequestURI());
            String viewName = handler.handleRequest(request, response);

            RequestDispatcher requestDispatcher = request.getRequestDispatcher(viewName);
            requestDispatcher.forward(request, response);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
        }
    }
}
