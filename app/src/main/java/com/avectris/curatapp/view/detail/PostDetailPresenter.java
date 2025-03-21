package com.avectris.curatapp.view.detail;

import android.content.Intent;

import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.view.base.BasePresenter;
import com.avectris.curatapp.vo.Post;

import java.io.IOException;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by thuongle on 2/15/16.
 */
class PostDetailPresenter extends BasePresenter<PostDetailView> {

    private final DataManager mDataManager;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    @Inject
    PostDetailPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscriptions != null) {
            mSubscriptions.clear();
            mSubscriptions = null;
        }
    }

    void getPostDetail(String apiCode, String postId) {
        checkViewAttached();
        mView.setButtonEnable(false);
        mSubscriptions.add(mDataManager
                .fetchPostDetail(apiCode, postId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(
                        response -> {
                            if (response.isSuccess()) {
                                mView.setButtonEnable(true);
                                mView.onPostDetailReturn(response.getPost());
                            } else {
                                mView.onRequestFailed(response.getMessage());
                            }
                        },
                        e -> {
                            mView.setButtonEnable(true);
                            if (e instanceof IOException) {
                                mView.onNetworkError();
                            } else {
                                mView.onGeneralError();
                            }
                        }));
    }

    void updatePost(String apiCode, String postId, Intent shareIntent) {
        checkViewAttached();
        mView.setButtonEnable(false);

        mSubscriptions.add(mDataManager
                .updatePosted(apiCode, postId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(response -> {
                            if (response.isSuccess()) {
                                mView.setButtonEnable(true);
                                mView.onUpdatePostSuccess(shareIntent);
                            } else {
                                mView.onRequestFailed(response.getMessage());
                            }
                        },
                        e -> {
                            mView.setButtonEnable(true);
                            if (e instanceof IOException) {
                                mView.onNetworkError();
                            } else {
                                mView.onGeneralError();
                            }
                        }));
    }

    public void trackPost(Post post) {
        checkViewAttached();
        mView.showProgress(true);

        mSubscriptions.add(mDataManager
                .trackPost(post)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(response -> {
                            if (response.isSuccess()) {
                                mView.onTrackPostSuccess(response.getMessage());
                            } else {
                                mView.onTrackPostFailed(response.getMessage());
                            }
                            mView.showProgress(false);
                        },
                        e -> {
                            mView.showProgress(false);
                            if (e instanceof IOException) {
                                mView.onNetworkError();
                            } else {
                                mView.onGeneralError();
                            }
                        }));
    }
}
