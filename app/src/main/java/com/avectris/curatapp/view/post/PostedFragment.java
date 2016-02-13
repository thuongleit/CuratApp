package com.avectris.curatapp.view.post;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.avectris.curatapp.R;
import com.avectris.curatapp.data.remote.vo.AccountPost;
import com.avectris.curatapp.di.scope.ActivityScope;
import com.avectris.curatapp.util.DialogFactory;
import com.avectris.curatapp.view.base.BaseFragment;
import com.pnikosis.materialishprogress.ProgressWheel;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thuongle on 1/13/16.
 */
public class PostedFragment extends BaseFragment implements PostView {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.progress_bar)
    ProgressWheel mProgressBar;

    @Inject
    PostedPresenter mPostedPresenter;
    @Inject
    @ActivityScope
    Context mContext;

    private RelativeLayout mView;

    public PostedFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.from(getActivity()).inflate(R.layout.fragment_upcoming, container, false);
        mView = (RelativeLayout) view;

        ButterKnife.bind(this, view);
        getComponent().inject(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setHasFixedSize(true);

        mPostedPresenter.attachView(this);
        mPostedPresenter.getPosts();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPostedPresenter.detachView();
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
        View emptyView = LayoutInflater.from(mContext).inflate(R.layout.view_empty_posts, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mView.addView(emptyView, layoutParams);
    }

    @Override
    public void onPostsShow(AccountPost accountPost) {
        RecyclerView.Adapter adapter = new PostsRecyclerAdapter(mContext, accountPost.getClient(), accountPost.getPosts());
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showNetworkFailed() {
        View viewNetworkError = LayoutInflater.from(mContext).inflate(R.layout.view_network_error, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        viewNetworkError.setOnClickListener(v -> {
            mPostedPresenter.getPosts();
            mView.removeView(viewNetworkError);
        });

        mView.addView(viewNetworkError, layoutParams);
    }

    @Override
    public void showGenericFailed() {
        DialogFactory.createGenericErrorDialog(mContext, R.string.dialog_message_verified_code_failed).show();
    }
}
