package com.avectris.curatapp.view.splash;

import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.data.exception.SessionNotFoundException;
import com.avectris.curatapp.di.scope.PerActivity;
import com.avectris.curatapp.view.base.BasePresenter;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by thuongle on 2/13/16.
 */
@PerActivity
class SplashPresenter extends BasePresenter<SplashView> {

    private final DataManager mDataManager;
    private CompositeSubscription mSubscriptions = new CompositeSubscription();

    @Inject
    public SplashPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscriptions != null) {
            mSubscriptions.unsubscribe();
        }
    }

    void restoreSession() {
        checkViewAttached();
        mSubscriptions.add(mDataManager
                .restoreSession()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseObservable -> {
                            mSubscriptions.add(responseObservable
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(
                                            response -> {
                                                if (response.isSuccess()) {
                                                    mView.onRestoreSessionSuccess(response.getAccount());
                                                } else {
                                                    mView.onNoActiveSession();
                                                }
                                            },
                                            e -> {
                                                if (e instanceof UnknownHostException || e instanceof SocketTimeoutException) {
                                                    mView.showNetworkFailed();
                                                } else {
                                                    mView.showGenericError();
                                                }
                                            }
                                    ));
                        },
                        e -> {
                            if (e instanceof SessionNotFoundException) {
                                mView.onNoActiveSession();
                            } else {
                                mView.showGenericError();
                            }
                        }));

    }
}
