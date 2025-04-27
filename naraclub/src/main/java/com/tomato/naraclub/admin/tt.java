package com.tomato.naraclub.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class tt {
    @GetMapping("/admin")
    public String adminHome(){
        return "admin/login/login-page";
    }

    @GetMapping("/admin/dashBoard")
    public String adminHome2(){
        return "admin/dashboard";
    }
}
