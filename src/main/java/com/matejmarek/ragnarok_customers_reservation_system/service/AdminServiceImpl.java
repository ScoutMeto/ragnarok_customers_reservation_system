package com.matejmarek.ragnarok_customers_reservation_system.service;

import com.matejmarek.ragnarok_customers_reservation_system.dto.AdminDTO;
import com.matejmarek.ragnarok_customers_reservation_system.entity.AdminEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.repository.AdminRepository;
import com.matejmarek.ragnarok_customers_reservation_system.exceptionHandler.DuplicateAdminEmailRegistrationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;

@Service
public class AdminServiceImpl implements AdminService, UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminServiceImpl(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AdminDTO createAdmin(AdminDTO model) {
        try {
            AdminEntity adminEntity = new AdminEntity();
            adminEntity.setAdminEmail(model.getAdminEmail());
            adminEntity.setPassword(passwordEncoder.encode(model.getPassword()));
            adminEntity.setNickname(model.getNickname());
            adminEntity.setAdmin(model.isAdmin()); // nastavení isAdmin


            adminEntity = adminRepository.save(adminEntity);

            AdminDTO savedUserDTO = new AdminDTO();
            savedUserDTO.setAdminId(adminEntity.getAdminId());
            savedUserDTO.setAdminEmail(adminEntity.getAdminEmail());

            System.out.println("Profil nového admina vytvořen:" + model);

                return savedUserDTO;

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            throw new DuplicateAdminEmailRegistrationException();
        }
    }

@Override
public UserDetails loadUserByUsername(String adminEmail) throws UsernameNotFoundException {
    AdminEntity admin = adminRepository.findByAdminEmail(adminEmail)
            .orElseThrow(() -> new UsernameNotFoundException("Admin " + adminEmail + " nenalezen."));

    return org.springframework.security.core.userdetails.User
            .withUsername(admin.getAdminEmail())
            .password(admin.getPassword())
            .roles("ADMIN") // důležité pro @PreAuthorize, autorizaci a přístup
            .build();

}

    @Override
    public void logoutAdmin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.logout();
        response.sendRedirect("/index.html");
    }

    @Override
    public void loginAdmin(AdminDTO adminDTO, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.login(adminDTO.getAdminEmail(), adminDTO.getPassword());
        response.sendRedirect("/index-adminPart.html");
    }

    @Override
    public ResponseEntity<AdminDTO> getCurrentAdminInfo(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();

        if (principal instanceof AdminEntity admin) {
            AdminDTO dto = new AdminDTO();
            dto.setAdminEmail(admin.getAdminEmail());
            dto.setAdminId(admin.getAdminId());
            dto.setAdmin(admin.isAdmin());

            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
