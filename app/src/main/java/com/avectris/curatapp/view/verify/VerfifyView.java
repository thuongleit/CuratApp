package com.avectris.curatapp.view.verify;

import com.avectris.curatapp.view.base.ErrorView;
import com.avectris.curatapp.vo.Account;

/**
 * Created by thuongle on 2/12/16.
 */
interface VerfifyView extends ErrorView {

    void showProgress(boolean show);

    void setButtonVerifyEnable(boolean enabled);

    void onCodeVerifySuccess(Account account);

    void onVerifiedFailed(String errorMsg);
}
