package com.matejmarek.ragnarok_customers_reservation_system.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CacheEvict {
    private final CacheManager cm;

    @Scheduled(fixedRate = 5 * 60 * 1000) // každých 5 minut
    public void sweep() {
        Optional.ofNullable(cm.getCache("trainingsByMonth"))
                .ifPresent(org.springframework.cache.Cache::clear);
    }
}
