package com.matejmarek.ragnarok_customers_reservation_system.controller;

import com.matejmarek.ragnarok_customers_reservation_system.dto.AdminDTO;
import com.matejmarek.ragnarok_customers_reservation_system.entity.AdminEntity;
import com.matejmarek.ragnarok_customers_reservation_system.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdminController {
    @Autowired
    AdminService adminService;

    @PostMapping({"api/registrationNewAdmin/", "api/registrationNewAdmin"})
    public AdminDTO addAdmin(@RequestBody @Valid AdminDTO adminDTO) {
        System.out.println("Požadavek na vytvoření nového administrátora (AdminController, addAdmin): " + adminDTO);
        return adminService.createAdmin(adminDTO);
    }

    @PostMapping({"api/loginAdmin/", "api/loginAdmin"})
    @ResponseBody
    public AdminDTO getAdmin(@RequestBody @Valid AdminDTO adminDTO, HttpServletRequest request) throws ServletException {
        System.out.println("Požadavek na přihlášení administrátora (AdminController, getAdmin");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication.getPrincipal() instanceof AdminEntity) {
            AdminEntity admin = (AdminEntity) authentication.getPrincipal();

            AdminDTO adminInfo = new AdminDTO();
            adminInfo.setAdminEmail(admin.getAdminEmail());
            adminInfo.setAdminId(admin.getAdminId());
            adminInfo.setAdmin(admin.isAdmin());

            return adminInfo;

        } else {
            throw new ServletException("Autentikace selhala.");

        }
            }




}
