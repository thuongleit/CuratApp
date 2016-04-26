package com.avectris.curatapp.view.verify;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import android.widget.Toast;
import com.avectris.curatapp.R;
import com.avectris.curatapp.config.Constant;
import com.avectris.curatapp.service.RegistrationIntentService;
import com.avectris.curatapp.util.DialogFactory;
import com.avectris.curatapp.view.base.BaseActivity;
import com.avectris.curatapp.view.main.MainActivity;
import com.avectris.curatapp.view.widget.CustomBottomLineEditText;
import com.avectris.curatapp.vo.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Created by thuongle on 1/13/16.
 */
public class VerifyActivity extends BaseActivity implements VerifyView {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    @Bind(R.id.input_email)
    CustomBottomLineEditText mInputEmail;
    @Bind(R.id.input_password)
    CustomBottomLineEditText mInputPassword;
    @Bind(R.id.button_verify)
    Button mButtonVerify;

    @Inject
    VerifyPresenter mVerifyPresenter;

    private ProgressDialog mProgressDialog;
    private Subscription mSubscription;
    private String mGcmToken;
    private boolean mReceiverToken = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mReceiverToken = true;
            mGcmToken = intent.getStringExtra(RegistrationIntentService.EXTRA_TOKEN);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        ButterKnife.bind(this);
        getComponent().inject(this);


        mVerifyPresenter.attachView(this);
        mInputPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                verify();
                return true;
            }
            return false;
        });
        mSubscription = Observable.combineLatest(RxTextView
                        .textChanges(mInputEmail)
                        .delay(200, TimeUnit.MILLISECONDS)
                        .flatMap(text -> Observable.just(text.length() > 0))
                , RxTextView.textChanges(mInputPassword)
                        .delay(200, TimeUnit.MILLISECONDS)
                        .flatMap(text -> Observable.just(text.length() > 0)),
                (result1, result2) -> result1 && result2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    setButtonVerifyEnable(result);
                });

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(Constant.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mVerifyPresenter.detachView();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
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
    public void onRequestSuccess(User user, String gcmToken) {
        mVerifyPresenter.fetchAccounts(user, gcmToken);
    }

    @Override
    public void onRequestFailed(String message) {
        DialogFactory.createGenericErrorDialog(this, message).show();
    }

    @Override
    public void onAccountsReturn() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNetworkError() {
        DialogFactory.createGenericErrorDialog(this, R.string.dialog_message_no_internet_working).show();
    }

    @Override
    public void onGeneralError() {
        DialogFactory.createSimpleOkErrorDialog(this, R.string.title_general_error, R.string.message_general_error).show();
    }

    @OnClick(R.id.button_verify)
    public void verify() {
        if (mGcmToken != null) {
            String email = mInputEmail.getText().toString().trim();
            String password = mInputPassword.getText().toString();
            if (validate(email, password)) {
                mVerifyPresenter.login(email, password, mGcmToken);
            }
        } else {
            if (mReceiverToken) {
                Toast.makeText(VerifyActivity.this, "Cannot register token in server", Toast.LENGTH_SHORT).show();
                onGeneralError();
            } else {
                DialogFactory.createGenericErrorDialog(this, R.string.message_wait_to_get_token).show();
            }
        }
    }

    private boolean validate(String email, String password) {
        boolean valid = true;
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            valid = false;
            mInputEmail.setError(getString(R.string.error_message_your_email_is_invalid));
            mInputEmail.requestFocus();
        } else {
            mInputEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            valid = false;
            mInputPassword.setError(getString(R.string.error_message_this_field_cannot_be_blank));
            mInputPassword.requestFocus();
        } else {
            mInputPassword.setError(null);
        }
        return valid;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Timber.e("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
