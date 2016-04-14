package com.avectris.curatapp.view.splash;

import com.avectris.curatapp.view.base.ErrorView;
import com.avectris.curatapp.vo.Account;

/**
 * Created by thuongle on 2/13/16.
 */
interface SplashView extends ErrorView {

    void onRestoreSessionSuccess(Account account);

    void onNoActiveSession(String errorMsg);
}
