package org.minjae.mvc.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc.view
 * @fileName : ModelAndView
 * @date : 2025-03-07
 * @description :
 * @AUTHOR : MinjaeKim
 */
public class ModelAndView {

    private Object view;
    private Map<String, Object> model = new HashMap<String, Object>();

    public ModelAndView(String viewName) {
        view = viewName;
    }

    public Map<String,?> getModel() {
        //불변으로
        return Collections.unmodifiableMap(model);
    }

    public String getViewName() {
        return (this.view instanceof String) ? (String) this.view : null;
    }
}
