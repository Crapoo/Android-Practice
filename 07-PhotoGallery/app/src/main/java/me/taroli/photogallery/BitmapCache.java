package me.taroli.photogallery;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by Matt on 26/07/15.
 */
public class BitmapCache {
    private static LruCache<String, Bitmap> cache;
    private static BitmapCache INSTANCE;

    public BitmapCache() {
        this.cache = new LruCache<String, Bitmap>(100);
    }

    public static BitmapCache getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new BitmapCache();
        }
        return INSTANCE;
    }

    public void put(String key, Bitmap value) {
        cache.put(key, value);
    }

    public Bitmap get(String key) {
        return cache.get(key);
    }
}
