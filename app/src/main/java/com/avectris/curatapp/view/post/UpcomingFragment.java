package com.avectris.curatapp.view.post;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.avectris.curatapp.R;
import com.avectris.curatapp.data.remote.vo.AccountPost;
import com.avectris.curatapp.di.scope.ActivityScope;
import com.avectris.curatapp.view.base.BaseFragment;
import com.avectris.curatapp.vo.Post;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thuongle on 1/13/16.
 */
public class UpcomingFragment extends BaseFragment implements PostView {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.progress_bar)
    ProgressWheel mProgressBar;
    @Bind(R.id.layout_content)
    RelativeLayout mLayoutContent;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Inject
    UpcomingPresenter mUpcomingPresenter;
    @Inject
    @ActivityScope
    Context mContext;
    @Inject
    Application mApplication;

    private int mCurrentPage = 0;

    private List<Post> mPosts;
    private Handler mHandler;
    private View mEmptyView;

    public UpcomingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.from(getActivity()).inflate(R.layout.fragment_upcoming, container, false);
        mPosts = new ArrayList<>();
        mHandler = new Handler();


        ButterKnife.bind(this, view);
        getComponent().inject(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);

        mUpcomingPresenter.attachView(this);
        mUpcomingPresenter.getPosts(mCurrentPage);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mUpcomingPresenter.getPostsForRefresh());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUpcomingPresenter.detachView();
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
    public void onEmptyPosts() {
        if (mEmptyView == null) {
            mEmptyView = LayoutInflater.from(mContext).inflate(R.layout.view_empty_posts, null);
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mLayoutContent.addView(mEmptyView, layoutParams);
    }

    @Override
    public void onPostsShow(AccountPost accountPost) {
        PostsRecyclerAdapter adapter;
        if (mRecyclerView.getAdapter() == null) {
            mPosts.addAll(accountPost.getPosts());
            adapter = new PostsRecyclerAdapter(mApplication, mContext, mRecyclerView, mPosts);
            mRecyclerView.setAdapter(adapter);
            adapter.setOnLoadMoreListener(() -> {
                //add null , so the adapter will check view_type and show progress bar at bottom
                mPosts.add(null);
                adapter.notifyItemInserted(mPosts.size() - 1);
                mUpcomingPresenter.getPosts(++mCurrentPage);
            });
        } else {
            adapter = (PostsRecyclerAdapter) mRecyclerView.getAdapter();
            mHandler.postDelayed(() -> {
                //   remove progress item
                mPosts.remove(mPosts.size() - 1);
                adapter.notifyItemRemoved(mPosts.size());
                //add items one by one
                for (int i = 0, size = accountPost.getPosts().size(); i < size; i++) {
                    mPosts.add(accountPost.getPosts().get(i));
                    adapter.notifyItemInserted(mPosts.size());
                }
                adapter.setLoaded(false);
                //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
            }, 500);
        }
    }

    @Override
    public void onRemoveBottomProgressBar() {
        if (mRecyclerView.getAdapter() != null) {
            PostsRecyclerAdapter adapter = (PostsRecyclerAdapter) mRecyclerView.getAdapter();
            mPosts.remove(mPosts.size() - 1);
            adapter.notifyItemRemoved(mPosts.size());

        }
    }

    @Override
    public void setNextPageLoaded(boolean loaded) {
        if (mRecyclerView.getAdapter() != null) {
            ((PostsRecyclerAdapter) mRecyclerView.getAdapter()).setLoaded(loaded);
        }
    }

    @Override
    public void removeSwipePullRefresh() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onPostsShowAfterRefresh(AccountPost accountPost) {
        List<Integer> changes = new ArrayList<>();

        List<Post> posts = accountPost.getPosts();
        if (posts != null && !posts.isEmpty()) {
            if (mEmptyView != null) {
                mLayoutContent.removeView(mEmptyView);
            }

            for (Post post : posts) {
                int count = 0;
                boolean hasObject = false;
                Iterator<Post> iterator = mPosts.iterator();
                do {
                    Post oldPost = iterator.next();
                    if (post.getId().equals(oldPost.getId())) {
                        if (!post.equals(oldPost)) {
                            changes.add(count);
                            mPosts.remove(count);
                            mPosts.add(count, post);
                        }
                        hasObject = true;
                        break;
                    }
                    count++;
                } while (iterator.hasNext());
                if (!hasObject) {
                    PostsRecyclerAdapter adapter = (PostsRecyclerAdapter) mRecyclerView.getAdapter();
                    mPosts.add(post);
                    adapter.notifyItemInserted(mPosts.size());
                }
            }

            if (mRecyclerView.getAdapter() == null) {
                PostsRecyclerAdapter adapter = new PostsRecyclerAdapter(mApplication, mContext, mRecyclerView, mPosts);
                mRecyclerView.setAdapter(adapter);
            } else {
                PostsRecyclerAdapter adapter = (PostsRecyclerAdapter) mRecyclerView.getAdapter();
                for (int i = 0, size = changes.size(); i < size; i++) {
                    Integer change = changes.get(i);
                    adapter.notifyItemChanged(change, mPosts.get(change));
                }
            }
        }
    }

    @Override
    public void showNetworkFailed() {
        View viewNetworkError = LayoutInflater.from(mContext).inflate(R.layout.view_network_error, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        viewNetworkError.setOnClickListener(v -> {
            mUpcomingPresenter.getPosts(mCurrentPage);
            mLayoutContent.removeView(viewNetworkError);
        });

        mLayoutContent.addView(viewNetworkError, layoutParams);
    }

    @Override
    public void showGenericError() {
        View viewGeneralError = LayoutInflater.from(mContext).inflate(R.layout.view_general_error, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        viewGeneralError.setOnClickListener(v -> {
            mUpcomingPresenter.getPosts(mCurrentPage);
            mLayoutContent.removeView(viewGeneralError);
        });

        mLayoutContent.addView(viewGeneralError, layoutParams);
    }
}
