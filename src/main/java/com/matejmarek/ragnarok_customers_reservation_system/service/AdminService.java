package com.matejmarek.ragnarok_customers_reservation_system.service;

import com.matejmarek.ragnarok_customers_reservation_system.dto.AdminDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

public interface AdminService extends UserDetailsService {

    AdminDTO createAdmin(AdminDTO adminDTO);

    void loginAdmin(AdminDTO adminDTO, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    void logoutAdmin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    ResponseEntity<AdminDTO> getCurrentAdminInfo(HttpServletRequest request);
}
