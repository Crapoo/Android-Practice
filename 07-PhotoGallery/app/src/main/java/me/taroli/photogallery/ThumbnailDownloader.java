package me.taroli.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matt on 25/07/15.
 */
public class ThumbnailDownloader<Token> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    /* TODO fix preload
        private static final int MESSAGE_PRELOAD = 1;
     */

    private Handler handler;
    private Handler responseHandler;
    private Listener<Token> listener;
    private Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());
    /* TODO fix preload
        private Map<String, String> preloadMap = Collections.synchronizedMap(new HashMap<String, String>());
    */

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        this.responseHandler = responseHandler;
    }

    @Override
    protected void onLooperPrepared() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    Token token = (Token) msg.obj;
                    Log.i(TAG, "Got a request for url: " + requestMap.get(token));
                    handleRequest(token);
                }
                /* TODO fix preload
                        else if (msg.what == MESSAGE_PRELOAD) {
                        String id = (String) msg.obj;
                        Log.i(TAG, "Got a preload request for url: " + preloadMap.get(id));
                        handlePreloadRequest(id);
                }*/
            }
        };
    }

    public void queueThumbnail(Token token, String url) {
        Log.i(TAG, "Got an url: " + url);
        requestMap.put(token, url);

        handler.obtainMessage(MESSAGE_DOWNLOAD, token).sendToTarget();
    }

    /* TODO fix preload
        public void preloadThumbnail(String id, String url) {
            Log.i(TAG, "Got an url for preload: " + url);
            preloadMap.put(id, url);

            handler.obtainMessage(MESSAGE_PRELOAD, id).sendToTarget();
        }
    */

    private void handleRequest(final Token token) {
        try {
            final String url = requestMap.get(token);
            if (url == null) {
                return;
            }
            final Bitmap bitmap;

            if (BitmapCache.getINSTANCE().get(url) != null) {
                bitmap = BitmapCache.getINSTANCE().get(url);
                Log.i(TAG, "Bitmap taken from cache");
            } else {
                byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
                bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                Log.i(TAG, "Bitmap created");
                BitmapCache.getINSTANCE().put(url, bitmap);
            }

            responseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (requestMap.get(token) != url) {
                        return;
                    }

                    requestMap.remove(token);
                    listener.onThumbnailDownloaded(token, bitmap);
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Error downloading image ", e);
        }
    }

    /* TODO fix preload
        private void handlePreloadRequest(final String id) {
            try {
                final String url = preloadMap.get(id);
                if (url == null) {
                    return;
                }

                final Bitmap bitmap;

                if (BitmapCache.getINSTANCE().get(url) != null) {
                    bitmap = BitmapCache.getINSTANCE().get(url);
                    Log.i(TAG, "Bitmap taken from cache");
                } else {
                    byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
                    bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                    Log.i(TAG, "Bitmap created");
                    BitmapCache.getINSTANCE().put(url, bitmap);
                }

                responseHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (preloadMap.get(id) != url) {
                            return;
                        }

                        preloadMap.remove(id);
                    }
                });
            } catch (IOException e) {
                Log.e(TAG, "Error downloading image ", e);
            }
        }
    */

    public void setListener(Listener<Token> listener) {
        this.listener = listener;
    }

    public void clearQueue() {
        handler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }

    public interface Listener<Token> {
        void onThumbnailDownloaded(Token token, Bitmap thumbnail);
    }
}
