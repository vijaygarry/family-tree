package com.neasaa.familytree.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
public class FrontendController {

    /**
     * What This Mapping Actually Does
     * This is part of a "catch-all" route used in Single Page Applications (SPAs) like React, where you want Spring Boot to serve index.html for unknown frontend routes (e.g. /family/25, /events), so the React Router can take over.
     *
     * Spring uses first match wins logic, and matches URLs in this order:
     * Exact controller mappings (e.g. /api/session or /api/getFamilyDetails, etc.)
     * ResourceHandlers (/uploads/**, /static/**, etc.)
     * Catch-all controllers like the FrontendController
     *
     * So if a request starts with /api/, Spring will:
     * See that you have a controller like @GetMapping("/api/getFamilyDetails")
     * Similarly:
     * /uploads/** is handled by your ResourceHandler
     * /favicon.ico, /static/** are handled by Spring's default static resource handling
     * If none of those match, it will fall through to this controller
     */
    @RequestMapping(value = { "/", "/{path:^(?!api|uploads|static|.*\\..*$).*$}/**"})
    public String forward(HttpServletRequest request) {
        return "forward:/index.html";
    }
}
