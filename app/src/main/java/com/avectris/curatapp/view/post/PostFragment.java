package com.avectris.curatapp.view.post;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.avectris.curatapp.R;
import com.avectris.curatapp.config.Constant;
import com.avectris.curatapp.di.scope.ActivityScope;
import com.avectris.curatapp.view.base.BaseFragment;
import com.avectris.curatapp.view.widget.DividerItemDecoration;
import com.avectris.curatapp.vo.Post;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thuongle on 1/13/16.
 */
public class PostFragment extends BaseFragment implements PostView, SwipeRefreshLayout.OnRefreshListener {
    private static final String ARG_CONTENT_MODE = "PostFragment.ARG_CONTENT_MODE";

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.progress_bar)
    ProgressWheel mProgressBar;
    @Bind(R.id.layout_content)
    RelativeLayout mLayoutContent;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Inject
    PostPresenter mPostPresenter;
    @Inject
    @ActivityScope
    Context mContext;
    @Inject
    Application mApplication;

    private List<Post> mPosts = new ArrayList<>();
    private int mCurrentPage = 0;
    private View mEmptyView;
    private View mErrorView;
    private int mContentMode;
    private BroadcastReceiver mNotificationReceiver;

    public PostFragment() {
    }

    public static Fragment createInstance(int contentMode) {
        PostFragment fragment = new PostFragment();
        Bundle arg = new Bundle();
        arg.putInt(ARG_CONTENT_MODE, contentMode);
        fragment.setArguments(arg);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.from(getActivity()).inflate(R.layout.fragment_posts, container, false);

        ButterKnife.bind(this, view);
        getComponent().inject(this);

        if (savedInstanceState == null) {
            mContentMode = getArguments().getInt(ARG_CONTENT_MODE);
        }
        mPostPresenter.attachView(this);
        mPostPresenter.setRequestMode(mContentMode);

        mNotificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mPostPresenter.getPostsForRefresh();
            }
        };

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));

        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mPosts.isEmpty()) {
            mPostPresenter.getPosts(0);
        } else {
            mPostPresenter.getPostsForRefresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager
                .getInstance(mContext)
                .registerReceiver(mNotificationReceiver, new IntentFilter(Constant.RECEIVE_NOTIFICATION));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager
                .getInstance(mContext)
                .unregisterReceiver(mNotificationReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mPostPresenter.detachView();
    }

    @Override
    public void onRefresh() {
        mPostPresenter.getPostsForRefresh();
    }

    @Override
    public void onPostsReturn(List<Post> posts) {
        PostRecyclerAdapter adapter;
        if (mRecyclerView.getAdapter() == null) {
            mPosts = posts;
            setupNewRecyclerView();
        } else {
            adapter = (PostRecyclerAdapter) mRecyclerView.getAdapter();
            //   remove progress item
            if (!mPosts.isEmpty()) {
                mPosts.remove(mPosts.size() - 1);
                adapter.notifyItemRemoved(mPosts.size());
            }
            //add items one by one
            for (int i = 0, size = posts.size(); i < size; i++) {
                mPosts.add(posts.get(i));
                adapter.notifyItemInserted(mPosts.size());
            }
            adapter.setLoaded(false);
        }
    }

    @Override
    public void onEmptyPostsReturn() {
        mCurrentPage = 0;
        if (mEmptyView == null) {
            mEmptyView = LayoutInflater.from(mContext).inflate(R.layout.view_empty_posts, null);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mLayoutContent.addView(mEmptyView, layoutParams);
        }
        if (!mPosts.isEmpty()) {
            PostRecyclerAdapter adapter = (PostRecyclerAdapter) mRecyclerView.getAdapter();
            adapter.clear();
        }
    }

    @Override
    public void onPostsReturnAfterRefresh(List<Post> posts) {
        mCurrentPage = 0;
        if (mRecyclerView.getAdapter() == null) {
            mPosts = posts;
            setupNewRecyclerView();
        } else {
            PostRecyclerAdapter adapter = (PostRecyclerAdapter) mRecyclerView.getAdapter();
            adapter.addItems(posts);
        }
    }

    @Override
    public void shouldRemoveEmptyView() {
        if (mEmptyView != null) {
            mLayoutContent.removeView(mEmptyView);
            mEmptyView = null;
        }
    }

    @Override
    public void shouldRemoveErrorView() {
        if (mErrorView != null) {
            mLayoutContent.removeView(mErrorView);
            mErrorView = null;
        }
    }

    @Override
    public void showNetworkFailedInRefresh() {
        Toast.makeText(mContext, "No Internet connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mProgressBar.spin();
        } else {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mProgressBar.stopSpinning();
        }
    }

    @Override
    public void onRemoveBottomProgressBar() {
        PostRecyclerAdapter adapter = (PostRecyclerAdapter) mRecyclerView.getAdapter();
        mPosts.remove(mPosts.size() - 1);
        adapter.notifyItemRemoved(mPosts.size());
    }

    @Override
    public void shouldStopPullRefresh() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void showNetworkFailed() {
        if (mErrorView == null) {
            mErrorView = LayoutInflater.from(mContext).inflate(R.layout.view_network_error, null);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mErrorView.setOnClickListener(v -> {
                new Handler().postDelayed(() -> mPostPresenter.getPosts(mCurrentPage), 500);
            });

            mLayoutContent.addView(mErrorView, layoutParams);
        }
    }

    @Override
    public void showGenericError() {
        if (mErrorView == null) {
            mErrorView = LayoutInflater.from(mContext).inflate(R.layout.view_general_error, null);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mErrorView.setOnClickListener(v -> {
                new Handler().postDelayed(() -> mPostPresenter.getPosts(mCurrentPage), 500);
            });

            mLayoutContent.addView(mErrorView, layoutParams);
        }
    }

    private void setupNewRecyclerView() {
        PostRecyclerAdapter adapter = new PostRecyclerAdapter(mApplication, mContext, mRecyclerView, mPosts);

        //no need to add load more listener when items < 10 (1 page in UI)
        if (mPosts.size() >= Constant.ITEM_PER_PAGE) {
            adapter.setOnLoadMoreListener(() -> {
                //add null , so the adapter will check view_type and show progress bar at bottom
                mPosts.add(null);
                adapter.notifyItemInserted(mPosts.size() - 1);
                mPostPresenter.getPosts(++mCurrentPage);
            });
        }
        mRecyclerView.setAdapter(adapter);
    }
}
