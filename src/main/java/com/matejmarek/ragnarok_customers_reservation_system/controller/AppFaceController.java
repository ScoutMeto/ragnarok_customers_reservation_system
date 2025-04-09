package com.matejmarek.ragnarok_customers_reservation_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppFaceController {

    @GetMapping("/")
    public String redirectToIndex() {
        return "redirect:/index.html";
    }

}

