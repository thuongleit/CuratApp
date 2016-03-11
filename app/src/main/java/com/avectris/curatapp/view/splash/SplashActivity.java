package com.avectris.curatapp.view.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.avectris.curatapp.R;
import com.avectris.curatapp.view.base.BaseActivity;
import com.avectris.curatapp.view.main.MainActivity;
import com.avectris.curatapp.view.verify.VerifyActivity;
import com.avectris.curatapp.vo.Account;

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
    public void onRestoreSessionSuccess(Account account) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_ACCOUNT, account);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNoActiveSession() {
        Intent intent = new Intent(this, VerifyActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showNetworkFailed() {
        showAlertDialog(getResources().getString(R.string.dialog_message_no_internet_working));
    }

    @Override
    public void showGenericError() {
        showAlertDialog("App couldn't launch now. Please try again later!");
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.dialog_action_ok, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                    startActivity(getReloadIntent());
                });
        alertDialog.setOnCancelListener(dialog -> {
            dialog.dismiss();
            finish();
            startActivity(getReloadIntent());
        });
        alertDialog.create().show();
    }
}
