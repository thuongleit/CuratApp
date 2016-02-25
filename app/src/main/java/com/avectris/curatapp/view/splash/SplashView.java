package com.avectris.curatapp.view.splash;

import com.avectris.curatapp.view.base.MvpView;

/**
 * Created by thuongle on 2/13/16.
 */
interface SplashView extends MvpView {

    void onRestoreSessionSuccess();

    void onNoSessionRecord();

    void onError();
}
