package com.avectris.curatapp.view.splash;

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
 * Created by thuongle on 2/12/16.
 */
class VerifyPresenter extends BasePresenter<VerfifyView> {

    private final DataManager mDataManager;
    private Subscription mSubscription = Subscriptions.empty();

    @Inject
    public VerifyPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }

    void verify(String verifyCode) {
        checkViewAttached();
        mSubscription = mDataManager
                .verify(verifyCode)
                .subscribeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            if (response.isSuccess()) {
                                mView.onCodeVerifySuccess(response.getAccount());
                            } else {
                                mView.onInvalidCode();
                            }
                        },
                        e -> {
                            if (e instanceof UnknownHostException || e instanceof SocketTimeoutException) {
                                mView.showNetworkFailed();
                            } else {
                                mView.showGenericFailed();
                            }
                        }
                );
    }
}
