package com.avectris.curatapp.view.verify;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.avectris.curatapp.R;
import com.avectris.curatapp.util.DialogFactory;
import com.avectris.curatapp.view.base.BaseActivity;
import com.avectris.curatapp.view.main.MainActivity;
import com.avectris.curatapp.view.widget.CustomBottomLineEditText;
import com.avectris.curatapp.vo.Account;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by thuongle on 1/13/16.
 */
public class VerifyActivity extends BaseActivity implements VerfifyView {
    @Bind(R.id.input_verify_code)
    CustomBottomLineEditText mInputCode;
    @Bind(R.id.button_verify)
    Button mButtonVerify;

    @Inject
    VerifyPresenter mVerifyPresenter;

    private ProgressDialog mProgressDialog;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        ButterKnife.bind(this);
        getComponent().inject(this);

        mVerifyPresenter.attachView(this);
        mInputCode.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                verify();
                return true;
            }
            return false;
        });
        mSubscription = RxTextView
                .textChanges(mInputCode)
                .delay(200, TimeUnit.MILLISECONDS)
                .map(charSequence -> charSequence.length() > 0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    mButtonVerify.setEnabled(aBoolean);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mVerifyPresenter.detachView();
        mSubscription.unsubscribe();
    }

    @Override
    public void showProgress(boolean show) {
        if (mProgressDialog == null) {
            mProgressDialog = DialogFactory.createProgressDialog(this, R.string.dialog_message_verifying);
        }
        if (show) {
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void setButtonVerifyEnable(boolean enabled) {
        mButtonVerify.setEnabled(enabled);
    }

    @Override
    public void onCodeVerifySuccess(Account account) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_ACCOUNT, account);
        startActivity(intent);
        finish();
    }

    @Override
    public void onVerifiedFailed(String errorMsg) {
        DialogFactory.createGenericErrorDialog(this, errorMsg).show();
    }

    @Override
    public void showNetworkFailed() {
        DialogFactory.createGenericErrorDialog(this, R.string.dialog_message_no_internet_working).show();
    }

    @Override
    public void showGenericError() {
        DialogFactory.createGenericErrorDialog(this, R.string.dialog_message_verified_code_failed).show();
    }

    @OnClick(R.id.button_verify)
    public void verify() {
        String verifyCode = mInputCode.getText().toString().trim();
        if (validate(verifyCode)) {
            mVerifyPresenter.verify(verifyCode);
        }
    }

    private boolean validate(String verifyCode) {
        boolean valid = true;
        if (TextUtils.isEmpty(verifyCode)) {
            valid = false;
            mInputCode.setError(getString(R.string.error_message_this_field_cannot_be_blank));
        } else {
            mInputCode.setError(null);
        }
        return valid;
    }
}
