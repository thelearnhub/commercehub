package com.thelearnhub.commercehub.inventory.lock;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 🔒 Redis Distributed Lock Pattern implementation using SETNX + TTL.
 * Prevents race conditions when concurrent requests try to reserve the last available item.
 */
@Component
public class RedisDistributedLock {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisDistributedLock(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private String getLockKey(String sku) {
        return "lock:inventory:" + sku;
    }

    public <T> T executeWithLock(String sku, Duration lockTimeout, Supplier<T> task) {
        String lockKey = getLockKey(sku);
        String lockValue = UUID.randomUUID().toString();

        Boolean acquired = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, lockTimeout);

        if (Boolean.TRUE.equals(acquired)) {
            try {
                return task.get();
            } finally {
                String currentValue = stringRedisTemplate.opsForValue().get(lockKey);
                if (lockValue.equals(currentValue)) {
                    stringRedisTemplate.delete(lockKey);
                }
            }
        } else {
            throw new IllegalStateException("Could not acquire inventory lock for SKU: " + sku + ". Please retry.");
        }
    }
}
