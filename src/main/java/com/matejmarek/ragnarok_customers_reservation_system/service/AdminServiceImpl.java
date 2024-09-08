package com.matejmarek.ragnarok_customers_reservation_system.service;

import com.matejmarek.ragnarok_customers_reservation_system.dto.AdminDTO;
import com.matejmarek.ragnarok_customers_reservation_system.entity.AdminEntity;
import com.matejmarek.ragnarok_customers_reservation_system.entity.repository.AdminRepository;
import com.matejmarek.ragnarok_customers_reservation_system.exceptionHandler.DuplicateAdminEmailRegistratrionExcepiton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
}
