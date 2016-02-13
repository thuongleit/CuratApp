package com.avectris.curatapp.view.splash;

import com.avectris.curatapp.view.base.ErrorView;
import com.avectris.curatapp.vo.Account;

/**
 * Created by thuongle on 2/12/16.
 */
interface VerfifyView extends ErrorView {

    void onCodeVerifySuccess(Account account);

    void onInvalidCode();
}
