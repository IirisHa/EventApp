package com.example.application.data.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        // Tämä ohjaa käyttäjän /access-denied sivulle aina, kun tapahtuu virhe
        return "redirect:/access-denied";
    }
}
