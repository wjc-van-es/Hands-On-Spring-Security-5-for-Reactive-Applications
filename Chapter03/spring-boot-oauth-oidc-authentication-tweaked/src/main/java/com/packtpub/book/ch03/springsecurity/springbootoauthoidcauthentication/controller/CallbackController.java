package com.packtpub.book.ch03.springsecurity.springbootoauthoidcauthentication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class CallbackController {

    @GetMapping("/callback")
    public String callback(Model model, Principal principal) {
        if(principal != null) {
            System.out.println(principal.getName());
            model.addAttribute("msg", "Welcome " + principal.getName() + " into Spring Boot OAuth and OIDC.");
        }
        return "callback";
    }
}
