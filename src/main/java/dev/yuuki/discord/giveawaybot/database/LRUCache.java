package dev.yuuki.discord.giveawaybot.database;

import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> {
    public static long getCurrentTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    private final long expireSeconds;
    private final LinkedHashMap<K, CacheEntity<V>> storage;

    private static class CacheEntity<V> {
        final V object;
        long lastAccessTimeStamp;

        public CacheEntity(V object) {
            this.object = object;
        }
    }

    public LRUCache(int size, int expireSeconds) {
        this.expireSeconds = expireSeconds;
        this.storage = new LinkedHashMap<>(size, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntity<V>> eldest){
                return size() > size;
            }
        };
    }

    @Nullable
    public synchronized V getCache(K key) {
        CacheEntity<V> entity = storage.get(key);
        if (entity == null) return null;
        long currentTimestamp = getCurrentTimeStamp();
        if (entity.lastAccessTimeStamp + expireSeconds < currentTimestamp) {
            deleteCache(key);
            return null;
        }
        entity.lastAccessTimeStamp = currentTimestamp;
        return entity.object;
    }

    public synchronized void setCache(K key, V value) {
        storage.put(key, new CacheEntity<V>(value));
    }

    public synchronized void deleteCache(K key) {
        storage.remove(key);
    }
}
