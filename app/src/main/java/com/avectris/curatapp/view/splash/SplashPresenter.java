package com.avectris.curatapp.view.splash;

import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.di.scope.PerActivity;
import com.avectris.curatapp.view.base.BasePresenter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

import javax.inject.Inject;
import java.io.IOException;

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
                .doOnNext(user -> {
                    if (user == null) {
                        mView.onNoSessionAvailable();
                    }
                })
                .flatMap(user -> mDataManager.fetchAccounts(user, null))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(
                        accountResponse -> {
                            if (accountResponse.isSuccess()) {
                                mView.onSessionRestore();
                            } else {
                                mView.onNoSessionAvailable();
                            }
                        },
                        e -> {
                            if (e instanceof IOException) {
                                mView.onNetworkError();
                            } else {
                                mView.onGeneralError();
                            }
                        });

    }
}
