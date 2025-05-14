package com.matejmarek.ragnarok_customers_reservation_system.controller;

import com.matejmarek.ragnarok_customers_reservation_system.dto.AdminDTO;
import com.matejmarek.ragnarok_customers_reservation_system.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.Setter;
import lombok.Getter;

import java.io.IOException;
import java.security.Principal;

@Setter
@Getter
@RestController
public class AdminController {
    @Autowired
    AdminService adminService;

    ////////////////////////////////////////////////////////////////////////////////////////

    @PostMapping({"api/registrationNewAdmin/", "api/registrationNewAdmin"})
    public AdminDTO addAdmin(@RequestBody @Valid AdminDTO adminDTO) {
        System.out.println("Požadavek na vytvoření nového administrátora (AdminController, addAdmin): " + adminDTO);
        return adminService.createAdmin(adminDTO);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    @PostMapping("/api/loginAdmin")
    public ResponseEntity<?> loginAdmin(@RequestBody AdminDTO adminDTO, HttpServletRequest request) {
        try {
            request.login(adminDTO.getAdminEmail(), adminDTO.getPassword());

            return ResponseEntity.ok("Přihlášení bylo úspěšné.");

        } catch (ServletException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Přihlášení selhalo.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    // Aktuálně přihlášený uživatel?
    @GetMapping("/api/whoami")
    public ResponseEntity<AdminDTO> whoAmI(HttpServletRequest request) {
        return adminService.getCurrentAdminInfo(request);
    }



    ////////////////////////////////////////////////////////////////////////////////////////

    @PostMapping({"api/logoutAdmin/", "api/logoutAdmin"})
    public void logout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        adminService.logoutAdmin(request, response);
    }

}
    ////////////////////////////////////////////////////////////////////////////////////////