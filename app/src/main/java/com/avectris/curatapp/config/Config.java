package com.avectris.curatapp.config;

import android.content.Context;
import android.content.SharedPreferences;

import com.avectris.curatapp.di.scope.ApplicationScope;
import com.avectris.curatapp.vo.Account;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by thuongle on 1/13/16.
 */
@Singleton
public class Config {
    private static final String KEY_CURRENT_CODE = "KEY_CURRENT_CODE";

    private final SharedPreferences mSharedPreferences;
    private Account mCurrentAccount;

    @Inject
    public Config(@ApplicationScope Context context) {
        mSharedPreferences = context.getSharedPreferences("curatapp_cfg", Context.MODE_PRIVATE);
    }

    public String getCurrentCode() {
        return mSharedPreferences.getString(KEY_CURRENT_CODE, null);
    }

    public void putCurrentCode(String code) {
        mSharedPreferences.edit().putString(KEY_CURRENT_CODE, code).apply();
    }

    public Account getCurrentAccount() {
        return mCurrentAccount;
    }

    public void setCurrentAccount(Account currentAccount) {
        this.mCurrentAccount = currentAccount;
    }
}