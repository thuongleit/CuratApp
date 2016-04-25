package com.avectris.curatapp.data;

import android.app.Application;
import android.text.TextUtils;

import com.avectris.curatapp.BuildConfig;
import com.avectris.curatapp.CuratApp;
import com.avectris.curatapp.config.Config;
import com.avectris.curatapp.config.Constant;
import com.avectris.curatapp.data.exception.SessionNotFoundException;
import com.avectris.curatapp.data.local.AccountModel;
import com.avectris.curatapp.data.local.UserModel;
import com.avectris.curatapp.data.remote.ApiHeaders;
import com.avectris.curatapp.data.remote.ErrorableResponse;
import com.avectris.curatapp.data.remote.PostService;
import com.avectris.curatapp.data.remote.SessionService;
import com.avectris.curatapp.data.remote.post.PostDetailResponse;
import com.avectris.curatapp.data.remote.post.PostResponse;
import com.avectris.curatapp.data.remote.verify.AccountResponse;
import com.avectris.curatapp.data.remote.verify.LoginResponse;
import com.avectris.curatapp.data.remote.verify.VerifyRequest;
import com.avectris.curatapp.data.remote.verify.VerifyResponse;
import com.avectris.curatapp.vo.Account;
import com.avectris.curatapp.vo.User;
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
    UserModel mUserModel;

    @Inject
    public DataManager(Application app) {
        ((CuratApp) app).getAppComponent().inject(this);
    }

    public Observable<User> restoreSession() {
        return Observable
                .just(mUserModel.getActiveUser())
                .filter(user -> user != null)
                .doOnNext(user -> mApiHeaders.buildSession(user.authToken, null, "123"));
    }

    public Observable<PostResponse> fetchPosts(int requestMode, int pageNumber) {
        if (mApiHeaders.getApiCode() == null) {
            Account account = mAccountModel.getCurrentAccount();
            if (account != null) {
                mApiHeaders.withApiCode(account.apiCode, "", "123");
            }
        }
        if (mApiHeaders.getApiCode() != null) {
            if (requestMode == Constant.POSTED_CONTENT_MODE) {
                return mPostService
                        .getPassedPosts(pageNumber);
            } else if (requestMode == Constant.UPCOMING_CONTENT_MODE) {
                return mPostService
                        .getUpcomingPosts(pageNumber);
            }
        }
        PostResponse failedResponse = new PostResponse();
        failedResponse.setResponse("error");
        return Observable.just(failedResponse);
    }

    public Observable<List<Account>> loadAccounts() {
        return Observable.just(mAccountModel.loadAll());
    }

    public Observable<PostDetailResponse> getPostDetail(String apiCode, String postId) {
//        String oldCode = mApiHeaders.getApiCode();
//        if (apiCode != null) {
//            mApiHeaders.withSession(apiCode);
//        }

        return mPostService
                .getPostDetail(postId)
                .doOnCompleted(() -> {
//                    if (!TextUtils.isEmpty(oldCode)) {
////                        mApiHeaders.withSession(oldCode);
//                    }
                });
    }

    private void cacheAccount(Account account) {
        mConfig.setCurrentAccount(account);

        String apiCode = account.apiCode;
        account.current = true;
    }

    public List<Observable<Boolean>> registerGcm(String token) {
        List<Observable<Boolean>> observables = new ArrayList<>();
        for (BaseModel model : mAccountModel.loadAll()) {
            Account account = (Account) model;

            if (account.enableNotification) {
                observables.add(mSessionService
                        .enablePushNotification(token == null ? account.gcmToken : token, String.valueOf(account.id))
                        .map(response -> {
                            if (response.isSuccess()) {
                                //saveOrUpdate to database
                                if (token != null) {
                                    mAccountModel.updateToken(account, token);
                                }
                                return true;
                            }
                            return false;
                        }));
            }
        }

        return observables;
    }

    public Observable<List<Account>> deleteAccount(List<Account> accounts, int position) {
        Account account = accounts.get(position);
        return mAccountModel
                .delete(account.id)
                .map(success -> {
                    if (success) {
                        accounts.remove(position);
                        removeAccount(account);
                        if (!accounts.isEmpty()) {
                            //next active account is the first account
                            Account nextAccount = accounts.get(0);
                            nextAccount.current = true;
                            cacheAccount(nextAccount);
                        }
                    }
                    return accounts;
                });

    }

    private void removeAccount(Account account) {
        mConfig.setCurrentAccount(null);
        mAccountModel.updateActiveAccount(account, false);

        mApiHeaders.logout();
    }

    public Observable<Boolean> enablePushNotification(Account account) {
        Account accountDb = mAccountModel.getAccountById(account.id);
        if (!accountDb.enableNotification) {
            return mSessionService
                    .enablePushNotification(accountDb.gcmToken, String.valueOf(account.id))
                    .map(response -> {
                        if (response.isSuccess()) {
                            mAccountModel.updatePushNotification(account, true);
                            return true;
                        }
                        return false;
                    });
        } else {
            return Observable.just(Boolean.TRUE);
        }
    }

    public Observable<Boolean> disablePushNotification(Account account) {
        Account accountDb = mAccountModel.getAccountById(account.id);
        if (accountDb.enableNotification) {
            return mSessionService
                    .disablePushNotification(accountDb.gcmToken, String.valueOf(account.id))
                    .map(response -> {
                        if (response.isSuccess()) {
                            mAccountModel.updatePushNotification(account, false);
                            //saveOrUpdate to database
                            return true;
                        }
                        return false;
                    });
        } else {
            return Observable.just(Boolean.TRUE);
        }
    }

    public Observable<Boolean> updateActiveAccount(Account account) {
        cacheAccount(account);
        mAccountModel.updateActiveAccount(account, true);
        return Observable.just(true);
    }

    public Observable<ErrorableResponse> updatePosted(String apiCode, String postId) {
//        String oldCode = mApiHeaders.getApiCode();
//        if (apiCode != null) {
//            mApiHeaders.withSession(apiCode);
//        }
//
//        return mPostService
//                .updateUserPosted(postId, 1)
//                .doOnCompleted(() -> {
//                    if (!TextUtils.isEmpty(oldCode)) {
//                        mApiHeaders.withSession(oldCode);
//                    }
//                });

        return Observable.empty();
    }

    public Observable<LoginResponse> login(String email, String password) {
        mApiHeaders.setLoginHeaders(email, password, String.valueOf(BuildConfig.VERSION_CODE));
        return mSessionService
                .login()
                .doOnNext(response -> {
                    if (response.isSuccess()) {
                        User user = response.user;
                        user.isActive = true;
                        mUserModel.save(user);
                        mApiHeaders.buildSession(user.authToken, null, String.valueOf(BuildConfig.VERSION_CODE));
                    }
                });
    }

    public Observable<AccountResponse> fetchAccounts(User user) {
        if (user != null) {
            mApiHeaders.buildSession(user.authToken, null, String.valueOf(BuildConfig.VERSION_CODE));
        }
        return mSessionService
                .fetchAccounts()
                .doOnNext(response -> {
                    if (response.isSuccess()) {
                        //the first account will be the current(selected) account in the app
                        if (!response.accounts.isEmpty()) {
                            response.accounts.get(0).current = true;
                        }
                        mAccountModel.save(response.accounts);
                    }
                });
    }
}
