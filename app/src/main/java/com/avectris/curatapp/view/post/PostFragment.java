package com.avectris.curatapp.view.post;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.avectris.curatapp.R;
import com.avectris.curatapp.config.Constant;
import com.avectris.curatapp.di.scope.ActivityScope;
import com.avectris.curatapp.view.base.BaseFragment;
import com.avectris.curatapp.view.widget.DividerItemDecoration;
import com.avectris.curatapp.vo.Post;
import com.pnikosis.materialishprogress.ProgressWheel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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

        if (savedInstanceState == null) {
            mContentMode = getArguments().getInt(ARG_CONTENT_MODE);
        } else {
            mContentMode = savedInstanceState.getInt(ARG_CONTENT_MODE);
        }

        ButterKnife.bind(this, view);
        getComponent().inject(this);

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_CONTENT_MODE, mContentMode);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        if (mContentMode == Constant.UPCOMING_CONTENT_MODE) {
            setUpItemTouchHelper();
            setUpAnimationDecoratorHelper();
        }

        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mSwipeRefreshLayout.setEnabled(layoutManager.findFirstCompletelyVisibleItemPosition() == 0);
            }
        });
    }

    private void setUpAnimationDecoratorHelper() {
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }

    private void setUpItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(mContext, R.drawable.ic_delete);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                PostRecyclerAdapter adapter = (PostRecyclerAdapter) mRecyclerView.getAdapter();
                mPostPresenter.deletePost(adapter.getItem(swipedPosition), swipedPosition);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for view-holder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

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
            adapter.canLoadMore(true);
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
    public void setViewCanLoadMore(boolean canLoad) {
        PostRecyclerAdapter adapter = (PostRecyclerAdapter) mRecyclerView.getAdapter();
        adapter.canLoadMore(canLoad);
    }

    @Override
    public void showResultMessage(String errorMsg) {
        Snackbar snackbar = Snackbar.make(mLayoutContent, errorMsg, Snackbar.LENGTH_SHORT);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();
    }

    @Override
    public void onDeleteSuccess(int position) {
        PostRecyclerAdapter adapter = (PostRecyclerAdapter) mRecyclerView.getAdapter();
        adapter.remove(position);

        if (adapter.getItemCount() == 0) {
            onEmptyPostsReturn();
        }
    }

    @Override
    public void recoverItem(Post item, int previousPos) {
        //just remove red background on unmoved item
        onDeleteSuccess(previousPos);
        PostRecyclerAdapter adapter = (PostRecyclerAdapter) mRecyclerView.getAdapter();
        adapter.addItem(item, previousPos);
    }

    @Override
    public void shouldStopPullRefresh() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onNetworkError() {
        if (mErrorView == null) {
            mErrorView = LayoutInflater.from(mContext).inflate(R.layout.view_network_error, null);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mErrorView.setOnClickListener(v -> new Handler().postDelayed(() -> mPostPresenter.getPosts(mCurrentPage), 500));

            mLayoutContent.addView(mErrorView, layoutParams);
        }
    }

    @Override
    public void onGeneralError() {
        if (mErrorView == null) {
            mErrorView = LayoutInflater.from(mContext).inflate(R.layout.view_general_error, null);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mErrorView.setOnClickListener(v -> new Handler().postDelayed(() -> mPostPresenter.getPosts(mCurrentPage), 500));

            mLayoutContent.addView(mErrorView, layoutParams);
        }
    }

    private void setupNewRecyclerView() {
        PostRecyclerAdapter adapter = new PostRecyclerAdapter(mApplication, mContext, mRecyclerView, mPosts);

        //no need to add load more listener when items < 10 (1 page in UI)
        adapter.setOnLoadMoreListener(() -> {
            //add null , so the adapter will check view_type and show progress bar at bottom
            mPosts.add(null);
            adapter.notifyItemInserted(mPosts.size() - 1);
            mPostPresenter.getPosts(++mCurrentPage);
        });
        mRecyclerView.setAdapter(adapter);
    }
}
