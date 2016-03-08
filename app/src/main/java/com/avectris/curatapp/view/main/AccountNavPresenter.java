package com.avectris.curatapp.view.main;

import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.di.scope.PerActivity;
import com.avectris.curatapp.view.base.BasePresenter;
import com.avectris.curatapp.vo.Account;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by thuongle on 2/13/16.
 */
@PerActivity
class AccountNavPresenter extends BasePresenter<AccountNavView> {

    private final DataManager mDataManager;
    private Subscription mSubscription = Subscriptions.empty();

    @Inject
    public AccountNavPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }

    void fetchAccounts() {
        mSubscription = mDataManager
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
                        e -> mView.onError());
    }
}
