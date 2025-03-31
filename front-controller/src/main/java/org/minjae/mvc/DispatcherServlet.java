package org.minjae.mvc;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.minjae.mvc.controller.HandlerKey;
import org.minjae.mvc.controller.RequestMethod;
import org.minjae.mvc.view.JspViewResolever;
import org.minjae.mvc.view.ModelAndView;
import org.minjae.mvc.view.View;
import org.minjae.mvc.view.ViewResolver;
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

//    private RequestMappingHandler requestMappingHandler;
    //Interface로
    private List<HandlerMapping> handlerMappings;
    private List<ViewResolver> viewResolvers;

    private List<HandlerAdapter> handlerAdapters;

    @Override
    public void init() throws ServletException {
        RequestMappingHandler requestMappingHandler = new RequestMappingHandler();
        requestMappingHandler.init();

        AnnorarionHandlerMapping ahm = new AnnorarionHandlerMapping("org.minjae");
        ahm.initialize();

        handlerMappings = List.of(requestMappingHandler, ahm);
        handlerAdapters= List.of(new SimpleControllerHandlerAdapter(), new AnnotationHandlerAdapter());
        viewResolvers = Collections.singletonList(new JspViewResolever());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        log.info("Dispatching request...");
        String requestUrI = request.getRequestURI();
        RequestMethod requestMethod = RequestMethod.valueOf(request.getMethod());
        try {
            //핸들러 서치
//            Controller handler = handlerMapping.findHandler(new HandlerKey(RequestMethod.valueOf(request.getMethod()), request.getRequestURI()));
            //service 에서 Object로 바꾸고 Anootation도 받을수 있도록 변경
            Object handler = handlerMappings.stream()
                .filter(hm -> hm.findHandler(new HandlerKey(requestMethod, requestUrI)) != null)
                .map(hm -> hm.findHandler(new HandlerKey(requestMethod, requestUrI)))
                .findFirst()
                .orElseThrow(() -> new ServletException("no Handler"));
//            findHandler(new HandlerKey(RequestMethod.valueOf(request.getMethod()), request.getRequestURI()));
//            String viewName = handler.handleRequest(request, response);

//            RequestDispatcher requestDispatcher = request.getRequestDispatcher(viewName);
//            requestDispatcher.forward(request, response);
            //핸들러 어답터 서치
            HandlerAdapter handlerAdapter = handlerAdapters.stream()
                .filter(ha->ha.supports(handler))
                .findFirst()
                .orElseThrow(()->new ServletException("No handler found for request: " + request.getRequestURI()));

            //어답터에서 찾아서 실행하면 ModelAndView Return
            ModelAndView modelAndView = handlerAdapter.handle(request, response, handler);

            //뷰 리졸버에서 뷰를 선택하여 렌더링
            for(ViewResolver viewResolver : viewResolvers) {
                View view = viewResolver.resolveView(modelAndView.getViewName());
                view.render(modelAndView.getModel(),request,response);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
        }
    }
}
