package com.avectris.curatapp.view.detail;

import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.view.base.BasePresenter;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by thuongle on 2/15/16.
 */
class PostDetailPresenter extends BasePresenter<PostDetailView> {

    private final DataManager mDataManager;
    private Subscription mSubscription = Subscriptions.empty();

    @Inject
    public PostDetailPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscription.unsubscribe();
    }

    void getPostDetail(String postId) {
        checkViewAttached();
        mSubscription = mDataManager
                .getPostDetail(postId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            if (response.isSuccess()) {
                                mView.onPostDetailReturn(response.getPost());
                            } else {
                                mView.onRequestFailed(response.getErrorMsg());
                            }
                        },
                        e -> {
                            if (e instanceof SocketTimeoutException || e instanceof UnknownHostException) {
                                mView.showNetworkFailed();
                            } else {
                                mView.showGenericError();
                            }
                        });
    }
}
