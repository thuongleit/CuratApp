package com.avectris.curatapp.data;

import android.app.Application;
import android.text.TextUtils;

import com.avectris.curatapp.CuratApp;
import com.avectris.curatapp.config.Config;
import com.avectris.curatapp.data.exception.SessionNotFoundException;
import com.avectris.curatapp.data.local.AccountModel;
import com.avectris.curatapp.data.remote.ApiHeaders;
import com.avectris.curatapp.data.remote.PostService;
import com.avectris.curatapp.data.remote.SessionService;
import com.avectris.curatapp.data.remote.post.PostDetailResponse;
import com.avectris.curatapp.data.remote.verify.VerifyRequest;
import com.avectris.curatapp.data.remote.verify.VerifyResponse;
import com.avectris.curatapp.data.remote.vo.AccountPost;
import com.avectris.curatapp.vo.Account;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by thuongle on 1/14/16.
 */
@Singleton
public class DataManager {

    @Inject
    SessionService mSessionService;
    @Inject
    PostService mPostService;
    @Inject
    Config mConfig;
    @Inject
    ApiHeaders mApiHeaders;
    @Inject
    AccountModel mAccountModel;

    @Inject
    public DataManager(Application app) {
        ((CuratApp) app).getAppComponent().inject(this);
    }

    public Observable<VerifyResponse> verify(String verifyCode) {
        VerifyRequest request = new VerifyRequest(verifyCode);

        return mSessionService
                .verify(request)
                .doOnNext(response -> {
                    cacheAccount(response.getAccount());
                    mAccountModel.save(response.getAccount());
                });

    }

    public Observable<Observable<VerifyResponse>> restoreSession() {
        return Observable
                .create((Observable.OnSubscribe<String>) subscriber -> {
                    String apiCode = mConfig.getCurrentCode();
                    if (TextUtils.isEmpty(apiCode)) {
                        subscriber.onError(new SessionNotFoundException());
                    } else {
                        subscriber.onNext(apiCode);
                    }
                    subscriber.onCompleted();
                })
                .map(verifyCode -> {
                    VerifyRequest request = new VerifyRequest(verifyCode);
                    return mSessionService
                            .verify(request)
                            .doOnNext(response -> cacheAccount(response.getAccount()));
                });
    }

    public Observable<AccountPost> getUpcomingPosts(int pageNumber) {
        return mPostService
                .getUpcomingPosts(pageNumber)
                .map(response -> response.getResult());

    }

    public Observable<AccountPost> getPassedPosts(int pageNumber) {
        return mPostService
                .getPassedPosts(pageNumber)
                .map(response -> response.getResult());
    }

    public Observable<List<? extends BaseModel>> getAccounts() {
        return mAccountModel.getAll();
    }

    public Observable<PostDetailResponse> getPostDetail(String postId) {
        return mPostService
                .getPostDetail(postId);
    }

    private void cacheAccount(Account account) {
        mConfig.setCurrentAccount(account);

        String apiCode = account.getApiCode();
        mConfig.putCurrentCode(apiCode);
        mApiHeaders.withSession(apiCode);
    }

    public List<Observable<Boolean>> registerTokenToGcm(String token) {
        List<Observable<Boolean>> observables = new ArrayList<>();
        for (BaseModel model : mAccountModel.getAllToList()) {
            Account account = (Account) model;

            observables.add(mSessionService
                    .enablePushNotification(token == null ? account.getGcmToken() : token, account.getAccountId() + "")
                    .map(response -> {
                        if (response.isSuccess()) {
                            //save to database
                            mAccountModel.updateToken(token);
                            return true;
                        }
                        return false;
                    }));
        }

        return observables;
    }

    public List<Observable<Boolean>> unregisterTokenToGcm() {
        List<Observable<Boolean>> observables = new ArrayList<>();
        for (BaseModel model : mAccountModel.getAllToList()) {
            Account account = (Account) model;

            observables.add(mSessionService
                    .disablePushNotification(account.getGcmToken(), account.getAccountId() + "")
                    .map(response -> {
                        if (response.isSuccess()) {
                            //save to database
                            return true;
                        }
                        return false;
                    }));
        }

        return observables;
    }
}
