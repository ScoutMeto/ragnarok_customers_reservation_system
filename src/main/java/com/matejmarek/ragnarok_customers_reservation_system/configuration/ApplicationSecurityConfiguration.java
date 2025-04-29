package com.matejmarek.ragnarok_customers_reservation_system.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class ApplicationSecurityConfiguration {

    private final UserDetailsService userDetailsService;

    public ApplicationSecurityConfiguration(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/api/**") // vypnout CSRF pro API
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        // frontend veřejně přístupný
                        .requestMatchers("/", "/index.html", "/index-adminPart.html", "/js/**", "/css/**").permitAll()
                        // veřejné API
                        .requestMatchers("/api/loginAdmin", "/api/registrationNewAdmin").permitAll()
                        // chráněné API pro přihlášené adminy
                        .requestMatchers("/api/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable()) // zcela vypnout přesměrování na /login
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/api/logoutAdmin"))
                        .logoutSuccessUrl("/index.html").permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}