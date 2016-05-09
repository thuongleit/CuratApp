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
    private final SharedPreferences mSharedPreferences;
    private static final String KEY_GCM_TOKEN = "gcm_token";
    private static final String KEY_USE_BETA = "use_beta_url";

    @Inject
    public Config(@ApplicationScope Context context) {
        mSharedPreferences = context.getSharedPreferences("curatapp_cfg", Context.MODE_PRIVATE);
    }

    public void saveGcmToken(String token) {
        mSharedPreferences.edit().putString(KEY_GCM_TOKEN, token).apply();
    }

    public String getGcmToken() {
        return mSharedPreferences.getString(KEY_GCM_TOKEN, "");
    }

    public boolean isUseBeta() {
        return mSharedPreferences.getBoolean(KEY_USE_BETA, false);
    }

    public void useBetaUrl() {
        mSharedPreferences.edit().putBoolean(KEY_USE_BETA, true).apply();
    }

    public void useProductionUrl() {
        mSharedPreferences.edit().putBoolean(KEY_USE_BETA, false).apply();
    }
}