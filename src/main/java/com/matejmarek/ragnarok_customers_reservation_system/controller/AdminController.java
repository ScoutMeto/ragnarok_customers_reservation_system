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

    //Tested by postman - OK
    @PostMapping({"api/registrationNewAdmin/", "api/registrationNewAdmin"})
    public AdminDTO addAdmin(@RequestBody @Valid AdminDTO adminDTO) {
        System.out.println("Požadavek na vytvoření nového administrátora (AdminController, addAdmin): " + adminDTO);
        return adminService.createAdmin(adminDTO);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    //Pro ostrý provoz - nefunguje pro testování skrz POSTMAN
//    @PostMapping("/api/loginAdmin")
//    public void loginAdmin(@RequestBody @Valid AdminDTO adminDTO,
//                           HttpServletRequest request,
//                           HttpServletResponse response) throws IOException {
//        try {
//            request.login(adminDTO.getAdminEmail(), adminDTO.getPassword());
//
//            // přesměrování na stránku po přihlášení
//            adminService.loginAdmin(adminDTO, request, response);
//
//        } catch (ServletException e) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Přihlášení selhalo");
//        }
//    }

    //Alternativa pro testování skrz postmana
    //Tested by postman - OK
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

    //Varianta pro ostrý provoz
    @GetMapping("/api/whoami")
    public ResponseEntity<AdminDTO> whoAmI(HttpServletRequest request) {
        return adminService.getCurrentAdminInfo(request);
    }

    //Varianta pro testování Postmanem
//    @GetMapping("/api/whoami")
//    public ResponseEntity<?> whoAmI(HttpServletRequest request) {
//        Principal principal = request.getUserPrincipal();
//
//        if (principal instanceof AdminEntity admin) {
//            AdminDTO dto = new AdminDTO();
//            dto.setAdminEmail(admin.getAdminEmail());
//            dto.setAdminId(admin.getAdminId());
//            dto.setAdmin(admin.isAdmin());
//            return ResponseEntity.ok(dto);
//        }
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Uživatel není přihlášen");
//    }


    ////////////////////////////////////////////////////////////////////////////////////////

    @PostMapping({"api/logoutAdmin/", "api/logoutAdmin"})
    public void logout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        adminService.logoutAdmin(request, response);
    }

}
    ////////////////////////////////////////////////////////////////////////////////////////