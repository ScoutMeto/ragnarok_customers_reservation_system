package com.matejmarek.ragnarok_customers_reservation_system.configuration;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
public class CacheKeyConfig {

    @Bean("dateRangeKeyGenerator")
    public KeyGenerator dateRangeKeyGenerator() {
        return (target, method, params) -> {
            // Oříznutí LocalDateTime na LocalDate
            Object[] normalized = Arrays.stream(params)
                    .map(p -> {
                        if (p instanceof LocalDateTime ldt) {
                            return ldt.toLocalDate();
                        }
                        return p;
                    })
                    .toArray();
            return new SimpleKey(normalized);
        };
    }
}