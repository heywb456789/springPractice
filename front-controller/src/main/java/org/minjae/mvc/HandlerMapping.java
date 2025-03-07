package org.minjae.mvc;

import org.minjae.mvc.controller.Controller;
import org.minjae.mvc.controller.HandlerKey;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc
 * @fileName : HandlerMapping
 * @date : 2025-03-07
 * @description :
 * @AUTHOR : MinjaeKim
 */
public interface HandlerMapping {

    Object findHandler(HandlerKey handlerKey);
}
