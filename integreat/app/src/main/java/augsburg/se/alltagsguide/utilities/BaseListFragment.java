package augsburg.se.alltagsguide.utilities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.utilities.ui.BaseAdapter;
import augsburg.se.alltagsguide.utilities.ui.BaseFragment;
import roboguice.inject.InjectView;

/**
 * Created by Daniel-L
 * on 30.11.2015
 */
public abstract class BaseListFragment<T> extends BaseFragment implements LoaderManager.LoaderCallbacks<List<T>>, SwipeRefreshLayout.OnRefreshListener {
    private static final String LOADING_TYPE_KEY = "FORCED";
    private static final String LIST_KEY = "LIST_KEY";

    @InjectView(R.id.recycler_view)
    protected UltimateRecyclerView mRecyclerView;
    protected StaggeredGridLayoutManager mLayoutManager;

    @NonNull protected List<T> mList = new ArrayList<>();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLayoutManager = new StaggeredGridLayoutManager(getRows(), StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setDefaultOnRefreshListener(this);
        mRecyclerView.setBackgroundColor(getBackgroundColor());
        addListener();
    }

    @ColorInt
    protected int getBackgroundColor() {
        return mPrefUtilities.getCurrentColor();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            Serializable serializable = savedInstanceState.getSerializable(LIST_KEY);
            if (serializable != null) {
                finishLoading((ArrayList<T>) serializable);
                return;
            }
        }
        refresh(LoadingType.FORCE_DATABASE);
        refresh(LoadingType.NETWORK_OR_DATABASE);
    }

    @Override
    public Loader<List<T>> onCreateLoader(int i, Bundle args) {
        LoadingType loadingType = (LoadingType) args.getSerializable(LOADING_TYPE_KEY);
        mRecyclerView.setRefreshing(true);
        return getLoader(loadingType);
    }

    public abstract Loader<List<T>> getLoader(LoadingType loadingType);

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(LIST_KEY, new ArrayList<>(mList));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        refresh(LoadingType.FORCE_NETWORK);
    }

    @Override
    public void onLoaderReset(Loader<List<T>> loader) {

    }

    public void refresh(LoadingType type) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(LOADING_TYPE_KEY, type);
        getLoaderManager().restartLoader(0, bundle, this);
    }

    @Override
    public void onLoadFinished(Loader<List<T>> loader, List<T> list) {
        finishLoading(list);
    }

    private void finishLoading(List<T> list) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setRefreshing(false);
            }
        }, 500);
        mList = list;
        setOrInitPageAdapter(mList);
        loaded();
    }

    public abstract void loaded();

    @Override
    public void networkStateSwitchedToOnline() {
        if (mList.isEmpty()) {
            mRecyclerView.setRefreshing(true);
            refresh(LoadingType.NETWORK_OR_DATABASE);
        }
        Log.d("EventPageOverview", "Network state switched");
    }

    protected void setOrInitPageAdapter(@NonNull List<T> elements) {
        if (elements.isEmpty()) {
            mRecyclerView.showEmptyView();
        } else {
            mRecyclerView.hideEmptyView();
        }
        BaseAdapter adapter = getOrCreateAdapter(elements);
        if (mRecyclerView.getAdapter() == null) {
            mRecyclerView.setAdapter(adapter);
        }
    }

    public abstract BaseAdapter getOrCreateAdapter(List<T> items);

    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private void addListener() {
        if (listener == null) {
            listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    switch (key) {
                        case PrefUtilities.MULTIPLE_COLUMNS_LANDSCAPE:
                            gridSettingsChanged();
                            break;
                        case PrefUtilities.MULTIPLE_COLUMNS_PORTRAIT:
                            gridSettingsChanged();
                            break;
                    }
                }
            };
            mPrefUtilities.addListener(listener);
        }
    }
    private void gridSettingsChanged() {
        updateColumnCount();
    }

    private void updateColumnCount() {
        if (mLayoutManager != null && isAdded()) {
            mLayoutManager.setSpanCount(getRows());
        }
    }

    public int getRows(){
        return getResources().getInteger(R.integer.grid_rows_page);
    }
}
