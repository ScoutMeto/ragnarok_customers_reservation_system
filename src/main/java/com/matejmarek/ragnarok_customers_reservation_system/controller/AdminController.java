package com.matejmarek.ragnarok_customers_reservation_system.controller;

import com.matejmarek.ragnarok_customers_reservation_system.dto.AdminDTO;
import com.matejmarek.ragnarok_customers_reservation_system.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    @Autowired
    AdminService adminService;

    @PostMapping("/registrationNewAdmin/api")
    public AdminDTO addAdmin(@RequestBody @Valid AdminDTO adminDTO) {
        System.out.println("Požadavek na vytvoření nového administrátora (AdminController, addAdmin): " + adminDTO);
        return adminService.createAdmin(adminDTO);
    }




}
