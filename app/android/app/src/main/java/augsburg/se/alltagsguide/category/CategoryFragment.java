package augsburg.se.alltagsguide.category;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersTouchListener;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Article;
import augsburg.se.alltagsguide.common.Category;
import augsburg.se.alltagsguide.utilities.BaseFragment;
import augsburg.se.alltagsguide.utilities.DividerDecoration;
import augsburg.se.alltagsguide.utilities.EmptyRecyclerView;
import augsburg.se.alltagsguide.utilities.PrefUtilities;
import roboguice.inject.InjectView;

public class CategoryFragment extends BaseFragment {
    private static final String ARG_CONTENT = "content";
    private Category mCategory;

    @InjectView(R.id.recycler_view)
    private EmptyRecyclerView mRecyclerView;

    @InjectView(R.id.emptyView)
    private View mEmptyView;

    private CategoryAdapter mCategoryAdapter;

    @Inject
    private PrefUtilities mPrefUtilities;

    private OnCategoryFragmentInteractionListener mListener;

    public static CategoryFragment newInstance(Category category) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONTENT, category);
        fragment.setArguments(args);
        return fragment;
    }

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategory = (Category) getArguments().getSerializable(ARG_CONTENT);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getActivity();
        mCategoryAdapter = new CategoryAdapter(mCategory, mListener, mPrefUtilities.getCurrentColor());
        //final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mCategoryAdapter);
        mRecyclerView.setAdapter(mCategoryAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
       // mRecyclerView.addItemDecoration(headersDecor);
       // mRecyclerView.addItemDecoration(new DividerDecoration(context));
        mRecyclerView.setEmptyView(mEmptyView);


      /*  StickyRecyclerHeadersTouchListener touchListener =
                new StickyRecyclerHeadersTouchListener(mRecyclerView, headersDecor);
        touchListener.setOnHeaderClickListener(
                new StickyRecyclerHeadersTouchListener.OnHeaderClickListener() {
                    @Override
                    public void onHeaderClick(View header, int position, long headerId) {
                        //TODO filter them

                    }
                });
        mCategoryAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        }); */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnCategoryFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnContentFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void changeCategory(Category category) {
        mCategoryAdapter.replace(category);
        if (category.getTitle() != null) {
            setTitle(category.getTitle());
        }
        if (category.getDescription() != null) {
            setSubTitle(category.getDescription());
        }
    }

    public interface OnCategoryFragmentInteractionListener {
        void onArticleClicked(Article article);

        void onCategoryClicked(Category category);
    }

}
