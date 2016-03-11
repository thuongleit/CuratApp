package com.avectris.curatapp.view.post;

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
class UpcomingPresenter extends BasePresenter<PostView> {

    private final DataManager mDataManager;
    private CompositeSubscription mSubscription;

    @Inject
    public UpcomingPresenter(DataManager dataManager) {
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
            mView.setNextPageLoaded(true);
        }
        mSubscription.add(mDataManager
                .getUpcomingPosts(pageNumber)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        accountPost -> {
                            if (accountPost == null || accountPost.getPosts() == null
                                    || accountPost.getPosts().isEmpty()) {
                                if (pageNumber == 0) {
                                    mView.onEmptyPosts();
                                } else {
                                    mView.onRemoveBottomProgressBar();
                                }
                            } else {
                                mView.onPostsShow(accountPost);
                            }
                        },
                        e -> {
                            if (pageNumber == 0) {
                                mView.showProgress(false);
                                if (e instanceof SocketTimeoutException || e instanceof UnknownHostException) {
                                    mView.showNetworkFailed();
                                } else {
                                    mView.showGenericError();
                                }
                            } else {
                                mView.onRemoveBottomProgressBar();
                            }
                        },
                        () -> mView.showProgress(false)));

    }

    public void getPostsForRefresh() {
        mSubscription.add(mDataManager
                .getUpcomingPosts(0)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        accountPost -> {
                            mView.removeSwipePullRefresh();
                            mView.onPostsShowAfterRefresh(accountPost);
                        },
                        e -> {
                            mView.removeSwipePullRefresh();
                        }));
    }
}
