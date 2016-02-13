package com.avectris.curatapp.data;

import android.app.Application;

import com.avectris.curatapp.CuratApp;
import com.avectris.curatapp.config.Config;
import com.avectris.curatapp.data.remote.SessionService;
import com.avectris.curatapp.data.remote.verify.VerifyRequest;
import com.avectris.curatapp.data.remote.verify.VerifyResponse;

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
    Config mConfig;

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

    private void cacheAccount(String apiCode) {
        mConfig.putApiCode(apiCode);
        mConfig.setCurrentCode(apiCode);
    }
}
