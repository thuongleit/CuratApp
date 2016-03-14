package com.avectris.curatapp.view.post;

import com.avectris.curatapp.config.Constant;
import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.view.base.BasePresenter;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by thuongle on 2/13/16.
 */
public class PostPresenter extends BasePresenter<PostView> {
    private final DataManager mDataManager;
    private CompositeSubscription mSubscription;
    private int mRequestMode;

    @Inject
    public PostPresenter(DataManager dataManager) {
        mDataManager = dataManager;
        mSubscription = new CompositeSubscription();
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscription.unsubscribe();
    }

    void getPosts(int pageNumber) {
        checkViewAttached();
        if (pageNumber == 0) {
            mView.showProgress(true);
        }
        mSubscription.add(mDataManager
                .getPosts(mRequestMode, pageNumber)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        posts -> {
                            if (posts.isEmpty()) {
                                if (pageNumber == 0) {
                                    mView.onEmptyPostsReturn();
                                } else {
                                    mView.onRemoveBottomProgressBar();
                                    mView.setViewCanLoadMore(false);
                                }
                            } else {
                                mView.shouldRemoveEmptyView();
                                mView.onPostsReturn(posts);
                                if (pageNumber == 0) {
                                    if (posts.size() >= Constant.ITEM_PER_PAGE) {
                                        mView.setViewCanLoadMore(true);
                                    }
                                }
                            }
                        },
                        e -> {
                            mView.shouldRemoveEmptyView();
                            if (pageNumber == 0) {
                                mView.showProgress(false);
                                if (e instanceof SocketTimeoutException || e instanceof UnknownHostException) {
                                    mView.showNetworkFailed();
                                } else {
                                    mView.showGenericError();
                                }
                            } else {
                                mView.onRemoveBottomProgressBar();
                                if (e instanceof SocketTimeoutException || e instanceof UnknownHostException) {
                                    mView.showNetworkFailedInRefresh();
                                }
                            }
                        },
                        () -> {
                            mView.showProgress(false);
                            mView.shouldRemoveErrorView();
                        }
                ));

    }

    public void getPostsForRefresh() {
        mSubscription.add(mDataManager
                .getPosts(mRequestMode, 0)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        posts -> {
                            if (posts.isEmpty()) {
                                mView.onEmptyPostsReturn();
                            } else {
                                mView.shouldRemoveEmptyView();
                                mView.onPostsReturnAfterRefresh(posts);
                                if (posts.size() >= Constant.ITEM_PER_PAGE) {
                                    mView.setViewCanLoadMore(true);
                                }
                            }
                        },
                        e -> {
                            mView.shouldStopPullRefresh();
                            if (e instanceof SocketTimeoutException || e instanceof UnknownHostException) {
                                mView.showNetworkFailedInRefresh();
                            }
                        }
                        , () -> {
                            mView.shouldStopPullRefresh();
                            mView.shouldRemoveErrorView();
                        }));
    }

    public void setRequestMode(int mode) {
        mRequestMode = mode;
    }
}
