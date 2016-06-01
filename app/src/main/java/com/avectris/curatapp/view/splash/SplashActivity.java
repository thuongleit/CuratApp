package com.avectris.curatapp.view.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import com.avectris.curatapp.R;
import com.avectris.curatapp.view.base.BaseActivity;
import com.avectris.curatapp.view.main.MainActivity;
import com.avectris.curatapp.view.verify.VerifyActivity;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import javax.inject.Inject;


public class SplashActivity extends BaseActivity implements SplashView {

    @Inject
    SplashPresenter mSplashPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

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
    public void onSessionRestore() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNoSessionAvailable() {
        Intent intent = new Intent(this, VerifyActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNetworkError() {
        showAlertDialog(R.string.dialog_error_title, R.string.dialog_message_no_internet_working);
    }

    @Override
    public void onGeneralError() {
        showAlertDialog(R.string.title_general_error, R.string.message_general_error);
    }

    private void showAlertDialog(int title, int message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_action_ok, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                    startActivity(getReloadIntent());
                })
                .setCancelable(false);
        alertDialog.create().show();
    }
}
