package com.avectris.curatapp.view.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import com.avectris.curatapp.view.base.BaseActivity;
import com.avectris.curatapp.view.main.MainActivity;
import com.avectris.curatapp.view.verify.VerifyActivity;
import com.avectris.curatapp.view.widget.MuliTextView;

import javax.inject.Inject;


public class SplashActivity extends BaseActivity implements SplashView {

    @Inject
    SplashPresenter mSplashPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getComponent().inject(this);
        mSplashPresenter.attachView(this);

        mSplashPresenter.restoreSession();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSplashPresenter.detachView();
    }

    @Override
    public void onRestoreSessionSuccess() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        Snac
    }

    @Override
    public void onNoSessionRecord() {
        Intent intent = new Intent(this, VerifyActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onError() {
        MuliTextView textView = new MuliTextView(this);
        textView.setText("");

        ViewGroup rootView = (ViewGroup) getWindow().getDecorView().getRootView();
        rootView.addView(textView);
    }
}
