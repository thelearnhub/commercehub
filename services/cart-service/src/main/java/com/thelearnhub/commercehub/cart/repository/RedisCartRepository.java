package com.thelearnhub.commercehub.cart.repository;

import com.thelearnhub.commercehub.cart.domain.Cart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class RedisCartRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final long ttlSeconds;

    public RedisCartRepository(
            RedisTemplate<String, Object> redisTemplate,
            @Value("${cart.ttl-seconds:604800}") long ttlSeconds
    ) {
        this.redisTemplate = redisTemplate;
        this.ttlSeconds = ttlSeconds;
    }

    private String getKey(String cartId) {
        return "cart:" + cartId;
    }

    public Optional<Cart> findById(String cartId) {
        Object obj = redisTemplate.opsForValue().get(getKey(cartId));
        if (obj instanceof Cart cart) {
            return Optional.of(cart);
        }
        return Optional.empty();
    }

    public void save(Cart cart) {
        String key = getKey(cart.getCartId());
        redisTemplate.opsForValue().set(key, cart, Duration.ofSeconds(ttlSeconds));
    }

    public void deleteById(String cartId) {
        redisTemplate.delete(getKey(cartId));
    }
}
