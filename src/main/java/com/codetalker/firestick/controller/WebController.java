package com.codetalker.firestick.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        // Return the Thymeleaf template name (templates/index.html)
        return "index";
    }

    @GetMapping("/search")
    public String search() {
        return "search";
    }

    @GetMapping("/indexing")
    public String indexing() { return "indexing"; }

    @GetMapping("/logs")
    public String logs() { return "logs"; }
}
