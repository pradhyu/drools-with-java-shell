package com.dmv.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

/**
 * Home controller for the main application interface
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "DMV Rules Engine");
        model.addAttribute("timestamp", LocalDateTime.now());
        return "index";
    }
}