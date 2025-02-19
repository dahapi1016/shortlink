package com.hapi.shortlink.admin.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RBloomFilterConfiguration {

    @Bean
    public RBloomFilter<String> userRegistrationBloomFilter (RedissonClient redissonClient) {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("userRegistrationBloomFilter");
        bloomFilter.tryInit(10_000_000L, 0.0001);
        return bloomFilter;
    }
}
