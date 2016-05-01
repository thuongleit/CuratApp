package com.avectris.curatapp.view.splash;

import com.avectris.curatapp.view.base.ErrorView;

/**
 * Created by thuongle on 2/13/16.
 */
interface SplashView extends ErrorView {

    void onSessionRestore();

    void onNoSessionAvailable();
}
