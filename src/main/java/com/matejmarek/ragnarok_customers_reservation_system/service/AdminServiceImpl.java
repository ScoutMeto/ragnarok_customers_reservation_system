package com.matejmarek.ragnarok_customers_reservation_system.service;

import com.matejmarek.ragnarok_customers_reservation_system.dto.AdminDTO;
import com.matejmarek.ragnarok_customers_reservation_system.entity.AdminEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.repository.AdminRepository;
import com.matejmarek.ragnarok_customers_reservation_system.exceptionHandler.DuplicateAdminEmailRegistratrionExcepiton;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public AdminDTO createAdmin(AdminDTO model) {
        try {
            AdminEntity adminEntity = new AdminEntity();
            adminEntity.setAdminEmail(model.getAdminEmail());
            adminEntity.setPassword(passwordEncoder.encode(model.getPassword()));

            adminEntity = adminRepository.save(adminEntity);

            AdminDTO savedUserDTO = new AdminDTO();
            savedUserDTO.setAdminId(adminEntity.getAdminId());
            savedUserDTO.setAdminEmail(adminEntity.getAdminEmail());

            System.out.println("Profil nového admina vytvořen:" + model + "Vyčkej na potvrzení autorizačních práv.");

                return savedUserDTO;

        } catch (DataIntegrityViolationException dataIntegrityViolationException) {
            throw new DuplicateAdminEmailRegistratrionExcepiton();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String adminName) throws UsernameNotFoundException {
        return adminRepository.findByEmail(adminName)
                .orElseThrow(() -> new UsernameNotFoundException("Jméno " + adminName + " nebylo nalezeno."));
    }

    // Metoda pro odhlášení administrátora
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            System.out.println("Administrátor úspěšně odhlášen.");
        } else {
            System.out.println("Administrátor nebyl přihlášen.");
        }
    }
}
