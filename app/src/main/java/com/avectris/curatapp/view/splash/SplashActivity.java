package com.avectris.curatapp.view.splash;

import android.content.Intent;
import android.os.Bundle;

import com.avectris.curatapp.R;
import com.avectris.curatapp.util.DialogFactory;
import com.avectris.curatapp.view.base.BaseActivity;
import com.avectris.curatapp.view.main.MainActivity;
import com.avectris.curatapp.view.verify.VerifyActivity;
import com.avectris.curatapp.vo.Account;

import javax.inject.Inject;


public class SplashActivity extends BaseActivity implements SplashView, VerfifyView {

    @Inject
    SplashPresenter mSplashPresenter;
    @Inject
    VerifyPresenter mVerifyPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getComponent().inject(this);
        mSplashPresenter.attachView(this);
        mVerifyPresenter.attachView(this);

        mSplashPresenter.restoreSession();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSplashPresenter.detachView();
        mVerifyPresenter.detachView();
    }

    @Override
    public void onRestoreSessionSuccess(String apiCode) {
        mVerifyPresenter.verify(apiCode);
    }

    @Override
    public void onNoSessionRecord() {
        Intent intent = new Intent(this, VerifyActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onError() {
        onNoSessionRecord();
    }

    @Override
    public void onCodeVerifySuccess(Account account) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_ACCOUNT, account);
        startActivity(intent);
        finish();
    }

    @Override
    public void onInvalidCode() {
        DialogFactory.createGenericErrorDialog(this, R.string.dialog_message_verify_code_invalid).show();
    }

    @Override
    public void showNetworkFailed() {
        DialogFactory.createGenericErrorDialog(this, R.string.dialog_message_no_internet_working).show();
    }

    @Override
    public void showGenericFailed() {
        DialogFactory.createGenericErrorDialog(this, R.string.dialog_message_verified_code_failed).show();
    }
}
