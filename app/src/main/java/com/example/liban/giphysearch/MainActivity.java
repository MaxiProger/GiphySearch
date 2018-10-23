package com.example.liban.giphysearch;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liban.giphysearch.mvp.model.ListData;
import com.example.liban.giphysearch.mvp.presenter.Presenter;
import com.example.liban.giphysearch.mvp.view.MainView;

public class MainActivity extends AppCompatActivity implements MainView, MenuItemCompat.OnActionExpandListener {

    private static final String TAG = "MainActivity";
    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private EditText mEditText;
    private Presenter mPresenter;
    private RecyclerAdapter mRecyclerAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTextView;
    private boolean isRefresh;

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bind();
        initListeners();
    }

    private void bind(){
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView = findViewById(R.id.recycler_id);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mTextView = findViewById(R.id.status_text_id);
        mSwipeRefreshLayout = findViewById(R.id.swipe_id);
        mPresenter = new Presenter(this, this);
        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);

    }
    private void initListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mPresenter.refresh();
        });
    }


    @Override
    public void onRequestTrending(ListData listData) {

        if (mRecyclerAdapter == null) {
            mRecyclerAdapter = new RecyclerAdapter(listData, this);
            mRecyclerAdapter.setTrendingContains(true);
            mRecyclerView.setAdapter(mRecyclerAdapter);
        }
        if (isRefresh) {
            mRecyclerAdapter.setTrendingContains(true);
            mRecyclerAdapter.clearData();
            mRecyclerAdapter.addNewGifs(listData.getData());

            setRefresh(false);
        }

        onScrollRecycler(listData, new AddListener() {
            @Override
            public void onEnd() {
                mOffsetCount = 10;
                mPresenter.requestTrending(mOffsetCount);
            }
        });
    }

    private int mOffsetCount = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainsearch, menu);
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint("Search");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mPresenter.requestSearch(newText,0);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestSearch(ListData listDataSearch) {
        mTextView.setText(getResources().getString(R.string.search_gif));
        mRecyclerAdapter.clearData();
        mRecyclerAdapter.addNewGifs(listDataSearch.getData());
        mRecyclerAdapter.setTrendingContains(true);

    }

    @Override
    public void onRefresh() {
        setRefresh(true);
        mTextView.setText(getResources().getString(R.string.trending_gifs));
        mTextView.setVisibility(View.VISIBLE);
        mRecyclerAdapter.setTrendingContains(true);
        mPresenter.requestTrending(0);
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onError(String messageError) {
        Toast.makeText(this, "You are not connected to the Internet", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showProgress(boolean flag) {
        if (flag)
            mProgressDialog = ProgressDialog.show(this, "Loading", "Wait");
        else
            mProgressDialog.dismiss();

    }


    private void onScrollRecycler(final ListData listData, AddListener addListener) {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {

                    if (mRecyclerAdapter.isTrendingContains()) {
                        addListener.onEnd();
                        mRecyclerAdapter.addNewGifs(listData.getData());
                    }
                }
            }
        });
    }


    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        mPresenter.requestTrending(0);
        return true;
    }
}
