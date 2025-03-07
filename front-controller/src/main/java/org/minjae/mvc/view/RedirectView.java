package org.minjae.mvc.view;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc.view
 * @fileName : RedirectView
 * @date : 2025-03-07
 * @description :
 * @AUTHOR : MinjaeKim
 */
public class RedirectView implements View {

    public static final String DEFAULT_REDIRECT_URL = "redirect:";
    private final String name;

    public RedirectView(String name) {
        this.name = name;
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(name.substring(DEFAULT_REDIRECT_URL.length()));
    }
}
