package me.taroli.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        new FetchItemsTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        gridView = (GridView) v.findViewById(R.id.gridView);
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

    void setupdAdapter() {
        if (getActivity() == null ||gridView == null) {
            return;
        }

        if (items != null) {
            gridView.setAdapter(new ArrayAdapter<GalleryItem>(getActivity(),
                    android.R.layout.simple_gallery_item, items));
        } else {
            gridView.setAdapter(null);
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
