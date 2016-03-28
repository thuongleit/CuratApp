package com.avectris.curatapp.view.main;

import android.support.v7.widget.SwitchCompat;

import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.di.scope.PerActivity;
import com.avectris.curatapp.view.base.BasePresenter;
import com.avectris.curatapp.vo.Account;

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
class AccountNavPresenter extends BasePresenter<AccountNavView> {

    private final DataManager mDataManager;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    @Inject
    public AccountNavPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscriptions != null) {
            mSubscriptions.unsubscribe();
        }
    }

    void fetchAccounts() {
        mSubscriptions.add(
                mDataManager
                        .getAccounts()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(models -> {
                                    if (models == null || models.isEmpty()) {
                                        mView.onNoAccountReturn();
                                    } else {
                                        mView.onAccountsReturn(models);
                                    }
                                },
                                e -> mView.onError("Cannot delete this account right now. Please try again later")));
    }

    public void deleteAccount(List<Account> accounts, int deletePosition) {
        checkViewAttached();
        mView.showProgress(true, "Processing...");
        mSubscriptions.add(
                mDataManager
                        .disablePushNotification(accounts.get(deletePosition))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                                    if (result) {
                                        performDeleteAccount(accounts, deletePosition);
                                    }
                                },
                                e -> {
                                    mView.showProgress(false, null);
                                    if (e instanceof SocketTimeoutException || e instanceof UnknownHostException) {
                                        mView.onNoInternetWhenDeleting();
                                    } else {
                                        Timber.e(e, "Error in delete account");
                                        mView.onError("Cannot delete this account right now. Please try again later");
                                    }
                                },
                                () -> mView.showProgress(false, null)));
    }

    private void performDeleteAccount(List<Account> accounts, int deletePosition) {
        mSubscriptions.add(mDataManager
                .deleteAccount(accounts, deletePosition)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(results -> {
                            if (results.isEmpty()) {
                                mView.onNoAccountAfterDelete();
                            } else {
                                mView.onDeleteAccountReturn(accounts.get(0));
                            }
                        }, e -> {
                            Timber.e(e, "Error in delete account");
                            mView.onError("Cannot delete this account right now. Please try again later");
                        }
                )

        );
    }

    public void enablePushNotification(SwitchCompat view, Account account) {
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

    public void disablePushNotification(SwitchCompat view, Account account) {
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

    public void chooseAccount(Account account) {
        mSubscriptions.add(
                mDataManager
                        .updateActiveAccount(account)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aBoolean -> {
                                    if (aBoolean) {
                                        mView.reloadActivity(account);
                                    }
                                }
                                , e -> {
                                }));
    }
}
