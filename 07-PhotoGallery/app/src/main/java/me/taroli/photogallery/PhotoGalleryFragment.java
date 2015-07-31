package me.taroli.photogallery;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;

import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.ArrayList;

/**
 * Created by Matt on 24/07/15.
 */
public class PhotoGalleryFragment extends VisibleFragment {
    private static final String TAG = "PhotoGalleryFragment";
    private static final int RECENT = 0;
    private static final int SEARCH = 1;

    private GridView gridView;
    private ArrayList<GalleryItem> items;
    private int currentPage;
    private int fetchedPage;
    private int currentAction;
    private boolean actionChanged;
    private int gridScrollPosition;
    private ThumbnailDownloader<ImageView> thumbnailThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);
        currentPage = 1;
        fetchedPage = 0;

        currentAction = RECENT;
        actionChanged = false;

        updateItems();

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

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GalleryItem item = items.get(position);

                Uri photoPageUri = Uri.parse(item.getPhotoPageUrl());
                Intent i = new Intent(getActivity(), PhotoPageActivity.class);
                i.setData(photoPageUri);

                startActivity(i);
            }
        });

        return v;
    }

    @Override
    @TargetApi(11)
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            MenuItem searchItem = menu.findItem(R.id.menu_item_search);
            SearchView searchView = (SearchView)searchItem.getActionView();

            SearchManager searchManager = (SearchManager)getActivity()
                    .getSystemService(Context.SEARCH_SERVICE);
            ComponentName name = getActivity().getComponentName();
            SearchableInfo searchInfo = searchManager.getSearchableInfo(name);

            searchView.setSearchableInfo(searchInfo);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity())) {
            toggleItem.setTitle(R.string.stop_polling);
        } else {
            toggleItem.setTitle(R.string.start_polling);
        }
    }

    @Override
    @TargetApi(11)
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                getActivity().onSearchRequested();
                return true;
            case R.id.menu_item_clear:
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putString(FlickrFetchr.PREF_SEARCH_QUERY, null)
                        .commit();
                updateItems();
                return true;
            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    getActivity().invalidateOptionsMenu();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    public void updateItems() {
        new FetchItemsTask().execute();
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
            Activity activity = getActivity();
            if (activity == null) {
                return new ArrayList<GalleryItem>();
            }

            String query = PreferenceManager.getDefaultSharedPreferences(activity)
                    .getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
            if (query == null) {
                if (currentAction != RECENT) {
                    currentPage = 1;
                    fetchedPage = 0;
                    currentAction = RECENT;
                    actionChanged = true;
                }
                return new FlickrFetchr().fetchItems(currentPage);
            } else {
                if (currentAction != SEARCH) {
                    currentPage = 1;
                    fetchedPage = 0;
                    currentAction = SEARCH;
                    actionChanged = true;
                }
                return new FlickrFetchr().search(query, currentPage);
            }

        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> galleryItems) {
            if (items == null || actionChanged) {
                items = galleryItems;
                actionChanged = false;
            } else {
                items.addAll(galleryItems);
            }
            setupdAdapter();
            gridView.setSelection(gridScrollPosition);
            fetchedPage++;
        }
    }
}
