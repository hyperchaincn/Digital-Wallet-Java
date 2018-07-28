package com.hyperchain.wallet.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@EnableAutoConfiguration
public class WebController {

    @GetMapping("/dashboard")
    public String dashboard(){
        return "/views/dashboard";
    }

    @GetMapping("/login")
    public String login(){
        return "/views/login";
    }

    @GetMapping("/register")
    public String register(){
        return "/views/register";
    }
}
