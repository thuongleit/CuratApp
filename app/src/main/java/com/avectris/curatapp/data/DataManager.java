package com.avectris.curatapp.data;

import android.app.Application;

import com.avectris.curatapp.CuratApp;
import com.avectris.curatapp.config.Config;
import com.avectris.curatapp.data.remote.ApiHeaders;
import com.avectris.curatapp.data.remote.PostService;
import com.avectris.curatapp.data.remote.SessionService;
import com.avectris.curatapp.data.remote.verify.VerifyRequest;
import com.avectris.curatapp.data.remote.verify.VerifyResponse;
import com.avectris.curatapp.data.remote.vo.AccountPost;

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
    public DataManager(Application app) {
        ((CuratApp) app).getAppComponent().inject(this);
    }

    public Observable<VerifyResponse> verify(String verifyCode) {
        VerifyRequest request = new VerifyRequest(verifyCode);

        return mSessionService
                .verify(request)
                .doOnNext(verifyResponse -> {
                    cacheAccount(verifyResponse.getAccount().getApiCode());
                });

    }

    public Observable<String> restoreSession() {
        return Observable.create((Observable.OnSubscribe<String>) subscriber -> {
            String apiCode = mConfig.getApiCode();
            if (apiCode == null) {
                subscriber.onNext("");
            } else {
                subscriber.onNext(apiCode);
                mConfig.setCurrentCode(apiCode);
            }
            subscriber.onCompleted();
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

    private void cacheAccount(String apiCode) {
        mConfig.putApiCode(apiCode);
        mConfig.setCurrentCode(apiCode);
        mApiHeaders.withSession(apiCode);
    }
}
