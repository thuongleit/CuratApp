package com.avectris.curatapp.view.main;

import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.di.scope.PerActivity;
import com.avectris.curatapp.view.base.BasePresenter;
import com.avectris.curatapp.vo.Account;

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
                                        mView.onAccountsReturn((List<Account>) models);
                                    }
                                },
                                e -> mView.onError("Cannot delete this account right now. Please try again later")));
    }

    public void deleteAccount(List<Account> accounts, int deletePosition) {
        checkViewAttached();
        mSubscriptions.add(mDataManager
                .deleteAccount(accounts, deletePosition)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(results -> {
                            if (results.isEmpty()) {
                                mView.onNoAccountAfterDelete();
                            } else {
                                mView.onAccountsReturn(accounts);
                            }
                        }, e -> {
                            Timber.e(e, "Error in delete account");
                            mView.onError("Cannot delete this account right now. Please try again later");
                        }
                )

        );
    }

    public void enablePushNotification(Account account) {
        mSubscriptions.add(
                mDataManager
                        .enablePushNotification(account)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                        }, e -> {
                        }));
    }

    public void disablePushNotification(Account account) {
        mSubscriptions.add(
                mDataManager.
                        disablePushNotification(account)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                        }, e -> {
                        }));
    }

    public void chooseAccount(Account account) {
        mSubscriptions.add(
                mDataManager
                        .verify(account.getApiCode())
                        .map(verifyResponse -> {
                            if (verifyResponse.isSuccess()) {
                                fetchAccounts();
                                return true;
                            }
                            return false;
                        })
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aBoolean -> {
                                }
                                , e -> {
                                }));
    }
}
