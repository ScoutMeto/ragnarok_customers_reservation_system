package com.matejmarek.ragnarok_customers_reservation_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppFaceController {

//    @GetMapping("/")
//    public String redirectToIndex() {
//        return "redirect:/index.html";
//    }

    @GetMapping("/")
    public String mainIndex() {
        return "forward:/index.html";
    }

    @GetMapping("/admin")
    public String adminIndex() {
        return "forward:/index-adminPart.html";
    }

    @GetMapping("/gdpr")
    public String gdprIndex() {
        return "forward:/index-gdpr.html";
    }

    @GetMapping("/provozni-navstevni-rad")
    public String rulesIndex() {
        return "forward:/index-operating_visitor_rules.html";
    }


}

