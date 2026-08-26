package com.example._faEmail.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> rateLimitScript;

    public RateLimitService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        String luaScript =
                "local current = redis.call('INCR', KEYS[1]) " +
                        "if current == 1 then " +
                        "    redis.call('EXPIRE', KEYS[1], ARGV[1]) " +
                        "end " +
                        "return current";

        this.rateLimitScript = new DefaultRedisScript<>(luaScript, Long.class);
    }

    public boolean isAllowed(String key, int limit, int windowSeconds) {
        Long count = redisTemplate.execute(
                rateLimitScript,
                Collections.singletonList(key),
                String.valueOf(windowSeconds)
        );

        return count != null && count <= limit;
    }
}
