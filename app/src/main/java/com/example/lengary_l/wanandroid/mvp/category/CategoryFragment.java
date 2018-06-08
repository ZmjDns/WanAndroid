package com.example.lengary_l.wanandroid.mvp.category;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.lengary_l.wanandroid.R;
import com.example.lengary_l.wanandroid.data.ArticleDetailData;
import com.example.lengary_l.wanandroid.interfaze.OnRecyclerViewItemOnClickListener;
import com.example.lengary_l.wanandroid.mvp.detail.DetailActivity;

import java.util.List;

public class CategoryFragment extends Fragment implements CategoryContract.View {

    private RecyclerView recyclerView;
    private LinearLayout emptyView;
    private Toolbar toolbar;
    private CategoryContract.Presenter presenter;
    private boolean isFirstLoad=true;
    private int categoryId;
    private String categoryName;
    private LinearLayoutManager layoutManager;
    private CategoryAdapter adapter;
    private static final int INDEX = 0;
    private int mListSize;
    private int currentPage;
    private int currentCategoryId;

    private static final String TAG = "CategoryFragment";

    public CategoryFragment(){

    }

    public static CategoryFragment newInstance(){
        return new CategoryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryId=getActivity().getIntent().getIntExtra(CategoryActivity.CATEGORY_ID,-1);
        categoryName = getActivity().getIntent().getStringExtra(CategoryActivity.CATEGORY_NAME);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad){
            Log.e(TAG, "onResume: " );
            presenter.getArticlesFromCatg(INDEX,categoryId,true,true);
            currentPage = INDEX;
            isFirstLoad = false;
        }else {
            presenter.getArticlesFromCatg(currentPage, categoryId, false,false);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unSubscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: " );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        initViews(view);
        CategoryActivity activity = (CategoryActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle(categoryName);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (layoutManager.findLastCompletelyVisibleItemPosition() == mListSize - 1) {
                        loadMore();
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void initViews(View view) {
        toolbar = view.findViewById(R.id.toolBar);
        recyclerView = view.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        emptyView = view.findViewById(R.id.empty_view);
    }

    @Override
    public void setPresenter(CategoryContract.Presenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public void showArticles(final List<ArticleDetailData> list) {
        if (adapter == null) {
            adapter = new CategoryAdapter(getContext(), list);
            adapter.setItemClickListener(new OnRecyclerViewItemOnClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Intent intent = new Intent(getContext(), DetailActivity.class);
                    intent.putExtra(DetailActivity.URL, list.get(position).getLink());
                    startActivity(intent);
                }
            });
            recyclerView.setAdapter(adapter);
        }else {
            adapter.updateData(list);
        }
        mListSize = list.size();
    }

    @Override
    public void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean isActive() {
        return isAdded() && isResumed();
    }

    private void loadMore() {
        currentPage += 1;
        presenter.getArticlesFromCatg(currentPage, categoryId, true,false);
    }
}
