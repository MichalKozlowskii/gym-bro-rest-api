package com.example.gym_bro_rest_api.services.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CacheUtils {
    private final CacheManager cacheManager;
    private final Map<String, Map<Long, Set<String>>> cacheKeyRegistry = new HashMap<>();

    public void track(String cacheName, Long userId, String key) {
        cacheKeyRegistry
                .computeIfAbsent(cacheName, k -> new HashMap<>())
                .computeIfAbsent(userId, k -> new HashSet<>())
                .add(key);
    }

    public void evict(String cacheName, Long userId) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) return;

        Map<Long, Set<String>> userKeys = cacheKeyRegistry.get(cacheName);
        if (userKeys == null || !userKeys.containsKey(userId)) return;

        for (String key : userKeys.get(userId)) {
            cache.evict(key);
        }

        userKeys.put(userId, new HashSet<>());
    }

    public void evictSingle(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }
}
