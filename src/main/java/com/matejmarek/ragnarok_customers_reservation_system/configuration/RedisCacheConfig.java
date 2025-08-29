//package com.matejmarek.ragnarok_customers_reservation_system.configuration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.interceptor.KeyGenerator;
//import org.springframework.cache.interceptor.SimpleKey;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.cache.RedisCacheManager;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.Map;
//
//@Configuration
//@EnableCaching
//public class RedisCacheConfig {
//
//    @Bean
//    public CacheManager cacheManager(RedisConnectionFactory cf) {
//        ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());
//        GenericJackson2JsonRedisSerializer ser = new GenericJackson2JsonRedisSerializer(om);
//
//        RedisCacheConfiguration cfg = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(3))
//                .disableCachingNullValues()
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(ser));
//
//        return RedisCacheManager.builder(cf)
//                .cacheDefaults(cfg)
//                .withInitialCacheConfigurations(Map.of("trainingsByMonth", cfg))
//                .build();
//    }
//
//    @Bean(name = "keyGenerator")
//    public KeyGenerator dateRangeKeyGenerator() {
//        return (t, m, p) -> new SimpleKey(
//                java.util.Arrays.stream(p)
//                        .map(v -> (v instanceof java.time.LocalDateTime ldt) ? ldt.toLocalDate() : v)
//                        .toArray()
//        );
//    }
//}