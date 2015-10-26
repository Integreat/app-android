package augsburg.se.alltagsguide.utilities.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class EmptyRecyclerView extends RecyclerView {
    private View mEmptyView;

    private boolean mObserverAttached = false;

    public EmptyRecyclerView(Context context) {
        this(context, null);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
        updateEmptyStatus(isEmpty());
    }

    private void updateEmptyStatus(boolean empty) {
        if (empty) {
            setVisibility(GONE);
            if (mEmptyView != null) {
                mEmptyView.setVisibility(VISIBLE);
            }
        } else {
            setVisibility(VISIBLE);
            if (mEmptyView != null) {
                mEmptyView.setVisibility(GONE);
            }
        }
    }

    public boolean isEmpty() {
        Adapter adapter = getAdapter();
        return adapter == null || adapter.getItemCount() == 0;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Adapter adapter = getAdapter();
        if (adapter != null && mObserverAttached) {
            adapter.unregisterAdapterDataObserver(mObserver);
            mObserverAttached = false;
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        adapter.registerAdapterDataObserver(mObserver);
        mObserverAttached = true;
        updateEmptyStatus(isEmpty());
    }

    private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateEmptyStatus(isEmpty());
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            updateEmptyStatus(isEmpty());
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            updateEmptyStatus(isEmpty());
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            updateEmptyStatus(isEmpty());
        }
    };
}
