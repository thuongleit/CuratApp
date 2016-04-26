package com.avectris.curatapp.view.main;

import android.support.v7.widget.SwitchCompat;

import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.di.scope.PerActivity;
import com.avectris.curatapp.view.base.BasePresenter;
import com.avectris.curatapp.vo.Account;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by thuongle on 2/13/16.
 */
@PerActivity
class AccountPresenter extends BasePresenter<AccountView> {

    private final DataManager mDataManager;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    @Inject
    AccountPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    protected void unsubscribeDataSource() {
        if (mSubscriptions != null) {
            mSubscriptions.clear();
            mSubscriptions = null;
        }
    }

    void loadAccounts() {
        checkViewAttached();
        mSubscriptions.add(
                mDataManager
                        .loadAccounts()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(accounts -> {
                                    if (accounts == null || accounts.isEmpty()) {
                                        mView.onEmptyAccounts();
                                    } else {
                                        mView.onAccountsReturn(accounts);
                                    }
                                },
                                e -> mView.onGeneralError()));
    }

    void enablePushNotification(SwitchCompat view, Account account) {
        mSubscriptions.add(
                mDataManager
                        .enablePushNotification(account)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (!result) {
                                mView.onEnableDisableNotificationFailed(view, false);
                            }
                        }, e -> {
                            mView.onEnableDisableNotificationFailed(view, false);
                        }));
    }

    void disablePushNotification(SwitchCompat view, Account account) {
        mSubscriptions.add(
                mDataManager.
                        disablePushNotification(account)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (!result) {
                                mView.onEnableDisableNotificationFailed(view, true);
                            }
                        }, e -> {
                            mView.onEnableDisableNotificationFailed(view, true);
                        }));
    }

    void chooseAccount(Account account) {
        checkViewAttached();
        mSubscriptions.add(
                mDataManager
                        .updateActiveAccount(account)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if (result) {
                                mView.refreshPage();
                            }
                        }, e -> {
                            mView.onGeneralError();
                        }));
    }

    void fetchAccounts() {
        checkViewAttached();
        mSubscriptions.add(
                mDataManager
                        .fetchAccounts(null, null)
                        .filter(respone -> respone.isSuccess())
                        .flatMap(response -> mDataManager.loadAccounts())
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(accounts -> mView.onAccountsReturn(accounts),
                                e -> {
                                    mView.removeSwipeLayout();
                                    if (e instanceof IOException) {
                                        mView.onNetworkError();
                                    } else {
                                        mView.onGeneralError();
                                    }
                                },
                                () -> mView.removeSwipeLayout()));
    }

    void logout() {
        checkViewAttached();
        mSubscriptions.add(
                mDataManager
                        .logout()
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                                    if (response.isSuccess()) {
                                        mView.logout();
                                    } else {
                                        mView.onRequestFailed(response.getMessage());
                                    }
                                },
                                e -> {
                                    if (e instanceof IOException) {
                                        mView.onNetworkError();
                                    } else {
                                        mView.onGeneralError();
                                    }
                                })
        );
    }

}
