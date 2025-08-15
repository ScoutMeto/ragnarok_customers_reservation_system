package com.matejmarek.ragnarok_customers_reservation_system.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory cf) {
//        RedisCacheConfiguration defaults = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(1))
//                .disableCachingNullValues()
//                .serializeValuesWith(
//                        RedisSerializationContext.SerializationPair
//                                .fromSerializer(new GenericJackson2JsonRedisSerializer())
//                );
//
//        return RedisCacheManager.builder(cf)
//                .cacheDefaults(defaults)
//                .withInitialCacheConfigurations(
//                        Map.of("trainingsByMonth", defaults)
//                )
//                .build();
//    }
        // TTL převezme z application.properties (spring.cache.redis.time-to-live)
        return RedisCacheManager.builder(cf).build();
    }

    @Bean(name = "keyGenerator")
    public KeyGenerator dateRangeKeyGenerator() {
        return (target, method, params) -> {
            Object[] normalized = Arrays.stream(params)
                    .map(p -> (p instanceof LocalDateTime ldt) ? ldt.toLocalDate() : p)
                    .toArray();
            return new SimpleKey(normalized);
        };
    }
}

//@Configuration
//@EnableCaching
//public class CacheConfig {
//}

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