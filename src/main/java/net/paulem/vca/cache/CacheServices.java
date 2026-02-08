package net.paulem.vca.cache;

import net.paulem.vca.cache.impl.HordeCacheService;

public final class CacheServices {
    private CacheServices() {}

    public static final HordeCacheService HORDE_CACHE_SERVICE = new HordeCacheService();
}
