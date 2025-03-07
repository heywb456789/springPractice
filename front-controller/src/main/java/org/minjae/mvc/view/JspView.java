package org.minjae.mvc.view;

import java.io.IOException;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc.view
 * @fileName : JspView
 * @date : 2025-03-07
 * @description :
 * @AUTHOR : MinjaeKim
 */
public class JspView implements View {

    private final String viewName;

    public JspView(String viewName) {
        this.viewName = viewName;
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
        model.forEach(request::setAttribute);

        RequestDispatcher rd = request.getRequestDispatcher(viewName);
        rd.forward(request, response);
    }
}
