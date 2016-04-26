package com.avectris.curatapp.view.verify;

import com.avectris.curatapp.view.base.ErrorView;
import com.avectris.curatapp.vo.User;

/**
 * Created by thuongle on 2/12/16.
 */
interface VerifyView extends ErrorView {

    void showProgress(boolean show);

    void setButtonVerifyEnable(boolean enabled);

    void onRequestSuccess(User user, String gcmToken);

    void onRequestFailed(String message);

    void onAccountsReturn();
}
