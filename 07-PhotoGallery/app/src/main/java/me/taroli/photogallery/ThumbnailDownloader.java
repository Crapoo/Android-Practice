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
    private static final int MESSAGE_PRELOAD = 1;

    private Handler handler;
    private Handler responseHandler;
    private Listener<Token> listener;

    private Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());

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
                } else if (msg.what == MESSAGE_PRELOAD) {
                    String url = (String) msg.obj;
                    Log.i(TAG, "Got a preload request for url: " + url);
                    handleRequest(url);
                }
            }
        };
    }

    public void queueThumbnail(Token token, String url) {
        if (url == null) {
            return;
        }
        Log.i(TAG, "Got an url: " + url);
        requestMap.put(token, url);

        handler.obtainMessage(MESSAGE_DOWNLOAD, token).sendToTarget();
    }

    public void queueThumbnail(String url) {
        handler.obtainMessage(MESSAGE_PRELOAD, url).sendToTarget();
    }

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

    private void handleRequest(final String url) {
        if (url == null) {
            return;
        }

        try {
            final Bitmap bitmap;
            if (BitmapCache.getINSTANCE().get(url) == null) {
                byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
                bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                BitmapCache.getINSTANCE().put(url, bitmap);
                Log.i(TAG, "Bitmap preloaded");
            } else {
                Log.i(TAG, "Bitmap not preloaded - already cached");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error downloading image ", e);
        }
    }

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
