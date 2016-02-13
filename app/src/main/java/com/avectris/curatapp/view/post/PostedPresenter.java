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
public class PostedPresenter extends BasePresenter<PostView> {
    private final DataManager mDataManager;
    private int mPageNumber = 0;
    private CompositeSubscription mSubscription;

    @Inject
    public PostedPresenter(DataManager dataManager) {
        mDataManager = dataManager;
        mSubscription = new CompositeSubscription();
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscription.unsubscribe();
    }

    void getPosts() {
        checkViewAttached();
        mView.showProgress(true);
        mSubscription.add(mDataManager
                .getUpcomingPosts(mPageNumber++)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        accountPost -> {
                            if (accountPost == null || accountPost.getPosts() == null
                                    || accountPost.getPosts().isEmpty()) {
                                mView.onEmptyPosts();
                            } else {
                                mView.onPostsShow(accountPost);
                            }
                        },
                        e -> {
                            if (e instanceof SocketTimeoutException || e instanceof UnknownHostException) {
                                mView.showNetworkFailed();
                            } else {
                                mView.showGenericFailed();
                            }
                        },
                        () -> {
                            mView.showProgress(false);
                        }));

    }
}
