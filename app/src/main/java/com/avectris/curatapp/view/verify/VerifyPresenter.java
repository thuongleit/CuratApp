package com.avectris.curatapp.view.verify;

import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.di.scope.PerActivity;
import com.avectris.curatapp.view.base.BasePresenter;
import com.avectris.curatapp.vo.User;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Created by thuongle on 2/12/16.
 */
@PerActivity
class VerifyPresenter extends BasePresenter<VerifyView> {

    private final DataManager mDataManager;
    private Subscription mSubscription = Subscriptions.empty();
    private Subscription mSubscription1 = Subscriptions.empty();

    @Inject
    VerifyPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    protected void unsubscribeDataSource() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }

        if (mSubscription1 != null) {
            mSubscription1.unsubscribe();
            mSubscription1 = null;
        }
    }

    void login(String email, String password, String gcmToken) {
        checkViewAttached();
        unsubscribeDataSource();
        mView.showProgress(true);
        mView.setButtonVerifyEnable(false);
        mSubscription = mDataManager
                .login(email, password, gcmToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(
                        response -> {
                            if (response.isSuccess()) {
                                mView.onRequestSuccess(response.user, gcmToken);
                            } else {
                                resetView();
                                mView.onRequestFailed(response.getMessage());
                            }
                        },
                        e -> {
                            resetView();
                            if (e instanceof IOException) {
                                mView.onNetworkError();
                            } else {
                                mView.onGeneralError();
                            }
                        });
    }

    void fetchAccounts(User user, String gcmToken) {
        checkViewAttached();
        unsubscribeDataSource();
        mSubscription1 = mDataManager
                .fetchAccounts(user, gcmToken)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            if (response.isSuccess()) {
                                mView.onAccountsReturn();
                            } else {
                                mView.onRequestFailed(response.getMessage());
                            }
                        },
                        e -> {
                            resetView();
                            if (e instanceof IOException) {
                                mView.onNetworkError();
                            } else {
                                mView.onGeneralError();
                            }
                        },
                        () -> resetView());
    }

    private void resetView() {
        mView.showProgress(false);
        mView.setButtonVerifyEnable(true);
    }
}
