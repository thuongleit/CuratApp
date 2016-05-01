package com.avectris.curatapp.view.splash;

import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.di.scope.PerActivity;
import com.avectris.curatapp.view.base.BasePresenter;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by thuongle on 2/13/16.
 */
@PerActivity
class SplashPresenter extends BasePresenter<SplashView> {

    private final DataManager mDataManager;
    private Subscription mSubscription = Subscriptions.empty();

    @Inject
    SplashPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    protected void unsubscribeDataSource() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
    }

    void restoreSession() {
        checkViewAttached();
        mSubscription = mDataManager
                .restoreSession()
                .count()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        count -> {
                            if (count > 0) {
                                mView.onSessionRestore();
                            } else {
                                mView.onNoSessionAvailable();
                            }
                        },
                        e -> {
                            mView.onGeneralError();
                        });

    }
}
