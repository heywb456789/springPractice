package org.minjae.mvc.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc.filter
 * @fileName : CharacterEncodingFilter
 * @date : 2025-03-07
 * @description :
 * @AUTHOR : MinjaeKim
 */
@WebFilter("/*")
public class CharacterEncodingFilter implements Filter {

    public static final String DEFAULT_CHARACTER = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        request.setCharacterEncoding(DEFAULT_CHARACTER);
        response.setCharacterEncoding(DEFAULT_CHARACTER);

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
