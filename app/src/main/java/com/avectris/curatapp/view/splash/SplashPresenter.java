package com.avectris.curatapp.view.splash;

import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.view.base.BasePresenter;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by thuongle on 2/13/16.
 */
class SplashPresenter extends BasePresenter<SplashView> {

    private final DataManager mDataManager;
    private Subscription mSubscription = Subscriptions.empty();

    @Inject
    public SplashPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }

    void restoreSession() {
        checkViewAttached();
        mSubscription = mDataManager
                .restoreSession()
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if (!result.isEmpty()) {
                                mView.onRestoreSessionSuccess(result);
                            } else {
                                mView.onNoSessionRecord();
                            }
                        },
                        error -> {
                            mView.onError();
                        });

    }
}
