package org.minjae.mvc.view;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc.view
 * @fileName : View
 * @date : 2025-03-07
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface View {
    void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException;
}
