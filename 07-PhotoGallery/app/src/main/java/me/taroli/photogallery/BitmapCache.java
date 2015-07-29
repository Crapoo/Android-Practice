package me.taroli.photogallery;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

/**
 * Created by Matt on 26/07/15.
 */
public class BitmapCache {
    private static LruCache<String, Bitmap> cache;
    private static BitmapCache INSTANCE;

    public BitmapCache() {
        int cacheSize = 4 * 1024 * 1024;
        this.cache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    return value.getByteCount();
                } else {
                    return value.getRowBytes() * value.getHeight();
                }
            }
        };
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
