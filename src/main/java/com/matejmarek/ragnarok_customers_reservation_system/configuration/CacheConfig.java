package com.matejmarek.ragnarok_customers_reservation_system.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {
}

//public class CacheConfig {
//
//    @Bean
//    public CacheManager cacheManager() {
//        Caffeine<Object, Object> spec = Caffeine.newBuilder()
//                .expireAfterWrite(Duration.ofMinutes(5))
//                .maximumSize(1_000)
//                .weakValues();
//
//        CaffeineCacheManager mgr = new CaffeineCacheManager("trainingsByMonth");
//        mgr.setCaffeine(Caffeine.newBuilder()
//                .maximumSize(999)
//                .expireAfterWrite(Duration.ofMinutes(3))
//                .recordStats());
//        return mgr;
//    }
//}
//
//public class CacheConfig {
//// 1) Caffeine CacheManager (vezme spec z application.properties)
//@Bean
//public CacheManager cacheManager() {
//    return new CaffeineCacheManager("trainingsByMonth");
//}
//
//// 2) Globální KeyGenerator – ořízne LocalDateTime na LocalDate
////    => "dnešní týden/měsíc" má stejný klíč, i když ping přijde v 10:00 nebo 10:30.
//@Bean(name = "keyGenerator")
//public KeyGenerator dateRangeKeyGenerator() {
//    return (target, method, params) -> {
//        Object[] normalized = java.util.Arrays.stream(params)
//                .map(p -> (p instanceof java.time.LocalDateTime ldt) ? ldt.toLocalDate() : p)
//                .toArray();
//        return new org.springframework.cache.interceptor.SimpleKey(normalized);
//    };
//}
//}