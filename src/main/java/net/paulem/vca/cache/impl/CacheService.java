package net.paulem.vca.cache.impl;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

/**
 * Abstract base class for implementing a caching service using Caffeine. This class provides
 * a convenient way to create and manage a {@link LoadingCache} with customizable expiration and
 * value loading strategies.
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of cached values
 */
public abstract class CacheService<K, V> {
    private final LoadingCache<K, V> cache;

    public CacheService(int duration, TimeUnit unit, CacheLoader<K, V> loader) {
        cache = Caffeine.newBuilder()
                .expireAfterWrite(duration, unit)
                .build(loader);
    }

    /**
     * Retrieves the cached value associated with the given key. If the key is not present
     * in the cache, it computes the value using the associated {@link CacheLoader}, stores
     * it in the cache, and then returns it.
     *
     * @param key the key whose associated value is to be retrieved
     * @return the cached value associated with the given key; if not present,
     *         the value is loaded, stored, and returned
     */
    public V get(K key) {
        // Store value if not present, else return cached value
        return cache.get(key);
    }
}
