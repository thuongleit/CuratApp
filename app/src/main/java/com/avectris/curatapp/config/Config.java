package com.avectris.curatapp.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by thuongle on 1/13/16.
 */
public class Config {
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NEW_JOB_RETRY_COUNT = "new_post_retry_count";
    private static final String KEY_API_URL = "api_url";

    private final SharedPreferences mSharedPreferences;

    public Config(Context context) {
        mSharedPreferences = context.getSharedPreferences("curatapp_cfg", Context.MODE_PRIVATE);
    }

    public String getUserId() {
        return mSharedPreferences.getString(KEY_USER_ID, null);
    }

    public void setUserId(String userId) {
        mSharedPreferences.edit().putString(KEY_USER_ID, userId).apply();
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
}