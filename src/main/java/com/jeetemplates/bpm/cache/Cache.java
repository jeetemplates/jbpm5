/**
 * 
 */
package com.jeetemplates.bpm.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle cache.
 * 
 * @author jeetemplates
 */
public final class Cache {

    /* ************************************************************** */
    /* Constructor */
    /* ************************************************************** */

    /**
     * Default constructor
     */
    private Cache() {

    }

    /* ************************************************************** */
    /* Cache names */
    /* ************************************************************** */

    /**
     * Enumeration of keys for cache regions
     */
    public enum CacheKey {
        MAP_RECEIVE_TASK_HANDLER;
    }

    /* ************************************************************** */
    /* Cache */
    /* ************************************************************** */

    /**
     * Application cache. Contains all value from referentiel.
     */
    private static Map<CacheKey, Map<String, Object>> cache = new HashMap<CacheKey, Map<String, Object>>();

    /* ************************************************************** */
    /* Methods */
    /* ************************************************************** */

    /**
     * Put in cache the value with key identifier
     * 
     * @param key
     *            : key
     * @param value
     *            : value
     */
    public static void put(CacheKey key, Map<String, Object> value) {
        cache.put(key, value);
    }

    /**
     * Get value from key identifier
     * 
     * @param key
     *            : key
     * @return value
     */
    public static Map<String, Object> get(CacheKey key) {
        return (cache.get(key) == null ? new HashMap<String, Object>() : cache.get(key));
    }

    /**
     * Flush cache
     */
    public static void flush() {
        cache = new HashMap<CacheKey, Map<String, Object>>();
    }

    /**
     * Remove a value
     * 
     * @param key
     *            : key of the value to remove
     */
    public static void remove(CacheKey key) {
        cache.remove(key);
    }

}
