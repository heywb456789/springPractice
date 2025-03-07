package org.minjae.mvc.view;

import static org.minjae.mvc.view.RedirectView.DEFAULT_REDIRECT_URL;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc.view
 * @fileName : JspViewResolever
 * @date : 2025-03-07
 * @description :
 * @AUTHOR : MinjaeKim
 */
public class JspViewResolever implements ViewResolver {

    @Override
    public View resolveView(String viewName) {
        if (viewName.startsWith(DEFAULT_REDIRECT_URL)) {
            return new RedirectView(viewName);
        }
        return new JspView(viewName+".jsp");
    }
}
