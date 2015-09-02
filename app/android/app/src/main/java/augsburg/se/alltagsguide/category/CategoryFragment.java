package augsburg.se.alltagsguide.category;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Article;
import augsburg.se.alltagsguide.common.Category;
import augsburg.se.alltagsguide.utilities.BaseFragment;

public class CategoryFragment extends BaseFragment {
    private static final String ARG_CONTENT = "content";
    private Category mCategory;
    private RecyclerView mRecyclerView;
    private CategoryAdapter mCategoryAdapter;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_article, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCategoryAdapter = new CategoryAdapter(mCategory, mListener);
        mRecyclerView.setAdapter(mCategoryAdapter);

        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCategoryFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
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
        setTitle(category.getTitle());
        setSubTitle(category.getDescription());
    }

    public interface OnCategoryFragmentInteractionListener {
        void onArticleClicked(Article article);
    }

}
