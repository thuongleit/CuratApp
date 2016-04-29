package com.avectris.curatapp.view.post;

import com.avectris.curatapp.config.Constant;
import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.view.base.BasePresenter;
import com.avectris.curatapp.vo.Post;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import javax.inject.Inject;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Created by thuongle on 2/13/16.
 */
class PostPresenter extends BasePresenter<PostView> {
    private final DataManager mDataManager;
    private CompositeSubscription mSubscription;
    private int mRequestMode;

    @Inject
    PostPresenter(DataManager dataManager) {
        mDataManager = dataManager;
        mSubscription = new CompositeSubscription();
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) {
            mSubscription.clear();
            mSubscription = null;
        }
    }

    void getPosts(int pageNumber) {
        checkViewAttached();
        if (pageNumber == 0) {
            mView.showProgress(true);
        }
        mSubscription.add(mDataManager
                .fetchPosts(mRequestMode, pageNumber)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(
                        response -> {
                            if (response.isSuccess()) {
                                List<Post> posts = response.getResult().getPosts();
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
                            } else {
                                mView.showResultMessage(response.getMessage());
                            }
                        },
                        e -> {
                            mView.shouldRemoveEmptyView();
                            if (pageNumber == 0) {
                                mView.showProgress(false);
                                if (e instanceof SocketTimeoutException || e instanceof UnknownHostException) {
                                    mView.onNetworkError();
                                } else {
                                    mView.onGeneralError();
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

    void getPostsForRefresh() {
        checkViewAttached();
        mSubscription.add(mDataManager
                .fetchPosts(mRequestMode, 0)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(
                        response -> {
                            if (response.isSuccess()) {
                                List<Post> posts = response.getResult().getPosts();
                                if (posts.isEmpty()) {
                                    mView.onEmptyPostsReturn();
                                } else {
                                    mView.shouldRemoveEmptyView();
                                    mView.onPostsReturnAfterRefresh(posts);
                                    if (posts.size() >= Constant.ITEM_PER_PAGE) {
                                        mView.setViewCanLoadMore(true);
                                    }
                                }
                            } else {
                                mView.showResultMessage(response.getMessage());
                            }
                        },
                        e -> {
                            mView.shouldStopPullRefresh();
                            if (e instanceof IOException) {
                                mView.showNetworkFailedInRefresh();
                            }
                        }
                        , () -> {
                            mView.shouldStopPullRefresh();
                            mView.shouldRemoveErrorView();
                        }));
    }

    void setRequestMode(int mode) {
        mRequestMode = mode;
    }

    void deletePost(Post item, int position) {
        mView.showProgress(true);
        checkViewAttached();
        mSubscription.add(
                mDataManager
                        .deleteAccount(item)
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                                    if (response.isSuccess()) {
                                        mView.onDeleteSuccess(position);
                                    } else {
                                        mView.showResultMessage(response.getMessage());
                                        mView.recoverItem(item, position);
                                    }
                                },
                                e -> {
                                    mView.showProgress(false);
                                    if (e instanceof IOException) {
                                        mView.showNetworkFailedInRefresh();
                                    } else {
                                        mView.showResultMessage("Request failed");
                                    }
                                    mView.recoverItem(item, position);
                                },
                                () -> mView.showProgress(false)));
    }
}
