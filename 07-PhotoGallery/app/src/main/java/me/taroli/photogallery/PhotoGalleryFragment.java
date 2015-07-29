package me.taroli.photogallery;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Matt on 24/07/15.
 */
public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = "PhotoGalleryFragment";

    private GridView gridView;
    private ArrayList<GalleryItem> items;
    private int currentPage = 1;
    private int fetchedPage = 0;
    private int gridScrollPosition;
    private ThumbnailDownloader<ImageView> thumbnailThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        new FetchItemsTask().execute();

        thumbnailThread = new ThumbnailDownloader<ImageView>(new Handler());
        thumbnailThread.setListener(new ThumbnailDownloader.Listener<ImageView>() {
            @Override
            public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
                if (isVisible()) {
                    imageView.setImageBitmap(thumbnail);
                }
            }
        });
        thumbnailThread.start();
        thumbnailThread.getLooper();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        gridView = (GridView) v.findViewById(android.R.id.list);
        gridView.setEmptyView(v.findViewById(android.R.id.empty));
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0 && currentPage == fetchedPage) {
                    gridScrollPosition = firstVisibleItem;
                    currentPage++;
                    new FetchItemsTask().execute();
                }
            }
        });
        setupdAdapter();

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        thumbnailThread.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thumbnailThread.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    void setupdAdapter() {
        if (getActivity() == null || gridView == null) {
            return;
        }

        if (items != null) {
            gridView.setAdapter(new GalleryItemsAdapter(items));
        } else {
            gridView.setAdapter(null);
        }
    }

    private class GalleryItemsAdapter extends ArrayAdapter<GalleryItem> {

        public GalleryItemsAdapter(ArrayList<GalleryItem> items) {
            super(getActivity(), 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.gallery_item, parent, false);
            }

            ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);
            imageView.setImageResource(R.drawable.placeholder);
            GalleryItem item = getItem(position);
            thumbnailThread.queueThumbnail(imageView, item.getUrl());

            int limitDown = position - 10 >= 0 ? position - 10 : 0;
            int limitUp = position + 10 <= items.size() ? position + 10 : items.size();
            /* Preload */
            for (int i = limitDown; i < limitUp; i++) {
                if (i == position) {
                    continue;
                }
                item = getItem(position);
                thumbnailThread.queueThumbnail(item.getUrl());
            }

            return convertView;
        }
    }

    class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {

        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetchr().fetchItems(currentPage);
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {
            if (items == null) {
                items = galleryItems;
            } else {
                items.addAll(galleryItems);
            }
            setupdAdapter();
            gridView.setSelection(gridScrollPosition);
            fetchedPage++;
        }
    }
}
