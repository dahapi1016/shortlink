package com.hapi.shortlink.project.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RBloomFilterConfiguration {

    /**
     * 短链接是否重复布隆过滤器
     */
    @Bean
    public RBloomFilter<String> shortLinkBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("shortLinkBloomFilter");
        bloomFilter.tryInit(100_000_000L, 0.001);
        return bloomFilter;
    }
}
