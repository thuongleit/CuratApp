package com.avectris.curatapp.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.avectris.curatapp.di.scope.ApplicationScope;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by thuongle on 1/13/16.
 */
@Singleton
public class Config {
    private static final String KEY_API_CODE = "api_code";
    private static final String KEY_NEW_JOB_RETRY_COUNT = "new_post_retry_count";
    private static final String KEY_API_URL = "api_url";

    private final SharedPreferences mSharedPreferences;
    private String mCurrentCode;

    @Inject
    public Config(@ApplicationScope Context context) {
        mSharedPreferences = context.getSharedPreferences("curatapp_cfg", Context.MODE_PRIVATE);
    }

    public String getApiCode() {
        return mSharedPreferences.getString(KEY_API_CODE, null);
    }

    public void putApiCode(String apiCode) {
        mSharedPreferences.edit().putString(KEY_API_CODE, apiCode).apply();
    }

    public int getNewPostRetryCount() {
        return mSharedPreferences.getInt(KEY_NEW_JOB_RETRY_COUNT, 20);
    }

    public void setNewPostRetryCount(int count) {
        mSharedPreferences.edit().putInt(KEY_NEW_JOB_RETRY_COUNT, count).apply();
    }

    public String getApiUrl() {
        return mSharedPreferences.getString(KEY_API_URL, "http://10.0.2.2:3000");
    }

    public void setApiUrl(String url) {
        mSharedPreferences.edit().putString(KEY_API_URL, url).apply();
    }

    public String getCurrentCode() {
        if(mCurrentCode == null){
            mCurrentCode = getApiCode();
        }
        return mCurrentCode;
    }

    public void setCurrentCode(String currentCode) {
        this.mCurrentCode = currentCode;
    }
}