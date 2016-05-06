package com.avectris.curatapp.data;

import android.app.Application;
import android.support.annotation.NonNull;
import com.avectris.curatapp.BuildConfig;
import com.avectris.curatapp.CuratApp;
import com.avectris.curatapp.config.Config;
import com.avectris.curatapp.config.Constant;
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
import com.avectris.curatapp.view.upload.UploadPostService;
import com.avectris.curatapp.vo.Account;
import com.avectris.curatapp.vo.Post;
import com.avectris.curatapp.vo.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
                .doOnNext(user -> {
                    if (user != null) {
                        mApiHeaders.withSession(user.authToken);
                    }
                });
    }

    public Observable<PostResponse> fetchPosts(int requestMode, int pageNumber) {
        Account account = mAccountModel.getCurrentAccount();
        buildSessionIfNeed(account.apiCode);
        if (mApiHeaders.getApiCode() != null) {
            if (requestMode == Constant.POSTED_CONTENT_MODE) {
                return mPostService
                        .getPassedPosts(pageNumber, account.gcmToken, getAppVersion(), getOs());
            } else if (requestMode == Constant.UPCOMING_CONTENT_MODE) {
                return mPostService
                        .getUpcomingPosts(pageNumber, account.gcmToken, getAppVersion(), getOs());
            }
        }
        PostResponse failedResponse = new PostResponse();
        failedResponse.setResponse("error");
        return Observable.just(failedResponse);
    }

    public Observable<List<Account>> loadAccounts() {
        User user = mUserModel.getActiveUser();
        return Observable
                .from(mAccountModel.loadAll())
                .filter(account -> user.email.equals(account.userEmail))
                .toList();
    }

    public Observable<PostDetailResponse> fetchPostDetail(String apiCode, String postId) {
        Account account;
        if (apiCode != null) {
            account = mAccountModel.getAccountByApiCode(apiCode);
        } else {
            account = mAccountModel.getCurrentAccount();
        }
        buildSessionIfNeed(account.apiCode);
        return mPostService
                .fetchPostDetail(postId, account.gcmToken, getAppVersion(), getOs())
                .doOnNext(response -> mApiHeaders.removeApiCode());
    }

    public List<Observable<Boolean>> registerGcm(String token) {
        List<Observable<Boolean>> observables = new ArrayList<>();
        List<Account> accounts = mAccountModel.loadAll();
        for (Account account : accounts) {
            account.gcmToken = token;
            account.update();
            buildSessionIfNeed(account.apiCode);
            if (account.enableNotification) {
                observables.add(mSessionService
                        .enablePushNotification(token == null ? account.gcmToken : token, String.valueOf(account.id))
                        .map(response -> {
                            if (response.isSuccess()) {
                                //saveOrUpdate to database
                                if (token != null) {
                                    account.gcmToken = token;
                                    mAccountModel.update(account);
                                }
                                return true;
                            }
                            return false;
                        })
                        .doOnNext(response -> mApiHeaders.removeApiCode()));
            }
        }

        return observables;
    }

    public Observable<Boolean> enablePushNotification(Account account) {
        buildSessionIfNeed(account.apiCode);
        Account accountDb = mAccountModel.getAccountById(account.id);
        if (accountDb != null && accountDb.gcmToken == null) {
            accountDb.gcmToken = mConfig.getGcmToken();
        }
        if (!accountDb.enableNotification) {
            return mSessionService
                    .enablePushNotification(accountDb.gcmToken, account.id)
                    .map(response -> {
                        if (response.isSuccess()) {
                            accountDb.enableNotification = true;
                            accountDb.update();
                            return true;
                        }
                        return false;
                    })
                    .doOnNext(response -> mApiHeaders.removeApiCode());
        } else {
            return Observable.just(Boolean.TRUE);
        }
    }

    public Observable<Boolean> disablePushNotification(Account account) {
        buildSessionIfNeed(account.apiCode);
        Account accountDb = mAccountModel.getAccountById(account.id);
        if (accountDb != null && accountDb.gcmToken == null) {
            accountDb.gcmToken = mConfig.getGcmToken();
        }
        if (accountDb.enableNotification) {
            return mSessionService
                    .disablePushNotification(accountDb.gcmToken, String.valueOf(account.id))
                    .map(response -> {
                        if (response.isSuccess()) {
                            accountDb.enableNotification = false;
                            accountDb.update();
                            //saveOrUpdate to database
                            return true;
                        }
                        return false;
                    })
                    .doOnNext(response -> mApiHeaders.removeApiCode());
        } else {
            return Observable.just(Boolean.TRUE);
        }
    }

    public Observable<Boolean> updateActiveAccount(Account account) {
        return Observable
                .just(mAccountModel.updateActiveAccount(account));
    }

    public Observable<ErrorableResponse> updatePosted(String apiCode, String postId) {
        Account account;
        if (apiCode != null) {
            account = mAccountModel.getAccountByApiCode(apiCode);
        } else {
            account = mAccountModel.getCurrentAccount();
        }
        buildSessionIfNeed(account.apiCode);
        return mPostService
                .updateUserPosted(postId, 1, account.gcmToken, getAppVersion(), getOs())
                .doOnNext(response ->
                        mApiHeaders.removeApiCode());
    }

    public Observable<LoginResponse> login(String email, String password, String gcmToken) {
        return mSessionService
                .login(email, password, gcmToken, getAppVersion(), getOs())
                .doOnNext(response -> {
                    if (response.isSuccess()) {
                        User user = response.user;
                        user.isActive = true;
                        mUserModel.save(user);
                        mApiHeaders.withSession(user.authToken);
                    }
                });
    }

    public Observable<AccountResponse> fetchAccounts(User user, String gcmToken) {
        Account currentAccount = mAccountModel.getCurrentAccount();
        if (mApiHeaders.getAuthToken() == null) {
            if (user == null) {
                user = mUserModel.getActiveUser();
            }
            if (gcmToken == null) {
                if (currentAccount != null) {
                    gcmToken = currentAccount.gcmToken;
                } else {
                    gcmToken = mConfig.getGcmToken();
                }
            }
            buildSessionIfNeed(currentAccount.apiCode);
            //reset user token
            mApiHeaders.withSession(user.authToken);
        }
        User finalUser = user;
        return mSessionService
                .fetchAccounts(gcmToken, getAppVersion(), getOs())
                .doOnNext(response -> {
                    if (response.isSuccess()) {
                        //if no active account, use the first account
                        if (currentAccount == null) {
                            //the first account will be the current(selected) account in the app
                            if (!response.accounts.isEmpty()) {
                                response.accounts.get(0).current = true;
                            }
                        } else if (currentAccount.userEmail.equals(finalUser.email)) {
                            for (Account account : response.accounts) {
                                if (currentAccount.id.equals(account.id)) {
                                    account.current = true;
                                    break;
                                }
                            }
                        } else {
                            if (!response.accounts.isEmpty()) {
                                response.accounts.get(0).current = true;
                                currentAccount.current = false;
                                currentAccount.save();
                            }
                        }

                        for (Account account : response.accounts) {
                            Account accountInDb = mAccountModel.getAccountById(account.id);
                            if (accountInDb != null) {
                                accountInDb.apiCode = account.apiCode;
                                accountInDb.active = account.active;
                                accountInDb.name = account.name;
                                accountInDb.current = account.current;
                                accountInDb.userEmail = finalUser.email;

                                accountInDb.update();
                            } else {
                                account.userEmail = finalUser.email;
                                account.save();
                            }
                        }
                    }
                });
    }

    public Observable<ErrorableResponse> logout() {
        buildSessionIfNeed(null);
        return mSessionService
                .logout()
                .doOnNext(response -> {
                    if (response.isSuccess()) {
                        User activeUser = mUserModel.getActiveUser();
                        activeUser.isActive = false;
                        activeUser.authToken = null;
                        mUserModel.save(activeUser);

                        //remove all header
                        mApiHeaders.logout();
                    }
                });
    }

    public void saveGcmToken(String token) {
        mConfig.saveGcmToken(token);
    }

    public Observable<ErrorableResponse> deletePost(Post item) {
        Account currentAccount = mAccountModel.getCurrentAccount();
        buildSessionIfNeed(currentAccount.apiCode);
        return mPostService
                .deletePost(currentAccount.gcmToken, getAppVersion(), getOs(), item.getId());
    }

    private void buildSessionIfNeed(String apicode) {
        if (mApiHeaders.getAuthToken() == null) {
            mApiHeaders.withSession(mUserModel.getActiveUser().authToken);
        }
        if (apicode != null) {
            mApiHeaders.withApiCode(apicode);
        } else {
            mApiHeaders.removeApiCode();
        }
    }

    public Observable<ErrorableResponse> uploadPost(int uploadMode, Account account, String path, String caption, String uploadTime) {
        File file = new File(path);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("uploadFile", file.getName(), requestFile);

        buildSessionIfNeed(null);
        switch (uploadMode) {
            case UploadPostService.REQUEST_ADD_TO_LIBRARY:
                return mPostService.addPostToLibrary(account.id, account.gcmToken, getAppVersion(), getOs(), caption, body);
            case UploadPostService.REQUEST_SELECT_EXACT_TIME:
                return mPostService.addPostOnExactTime(account.id, account.gcmToken, getAppVersion(), getOs(), caption, uploadTime, body);
            case UploadPostService.REQUEST_ADD_TO_SCHEDULE:
                return mPostService.addPostToSchedule(account.id, account.gcmToken, getAppVersion(), getOs(), caption, body);

        }
        return Observable.empty();
    }

    private String getOs() {
        return ApiHeaders.OS.ANDROID.getName();
    }

    @NonNull
    private String getAppVersion() {
        return String.valueOf(BuildConfig.VERSION_CODE);
    }
}
