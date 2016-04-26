package com.avectris.curatapp.view.base;

/**
 * Created by thuongle on 2/12/16.
 */
public interface ErrorView extends MvpView {

    void onNetworkError();

    void onGeneralError();
}
