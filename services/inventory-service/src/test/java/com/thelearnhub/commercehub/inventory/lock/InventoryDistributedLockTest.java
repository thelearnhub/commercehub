package com.thelearnhub.commercehub.inventory.lock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class InventoryDistributedLockTest {

    private StringRedisTemplate stringRedisTemplate;
    private ValueOperations<String, String> valueOperations;
    private RedisDistributedLock lock;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        stringRedisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        lock = new RedisDistributedLock(stringRedisTemplate);
    }

    @Test
    void executeWithLockAcquiresAndReleasesLockSuccessfully() {
        String sku = "SKU-IPHONE-15";
        when(valueOperations.setIfAbsent(eq("lock:inventory:" + sku), any(String.class), any(Duration.class)))
                .thenReturn(true);

        String result = lock.executeWithLock(sku, Duration.ofSeconds(5), () -> "STOCK_RESERVED");

        assertThat(result).isEqualTo("STOCK_RESERVED");
        verify(valueOperations).setIfAbsent(eq("lock:inventory:" + sku), any(String.class), eq(Duration.ofSeconds(5)));
    }

    @Test
    void executeWithLockThrowsExceptionWhenLockCannotBeAcquired() {
        String sku = "SKU-IPHONE-15";
        when(valueOperations.setIfAbsent(eq("lock:inventory:" + sku), any(String.class), any(Duration.class)))
                .thenReturn(false);

        assertThatThrownBy(() -> lock.executeWithLock(sku, Duration.ofSeconds(5), () -> "FAIL"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Could not acquire inventory lock");
    }
}
