package org.minjae.controller;

import org.minjae.annotation.Controller;
import org.minjae.annotation.Inject;
import org.minjae.service.UserService;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.controller
 * @fileName : UserController
 * @date : 2025-03-31
 * @description :
 * @AUTHOR : MinjaeKim
 */
@Controller
public class UserController {

    private final UserService userService;

    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    public UserService getUserService() {
        return userService;
    }
}
