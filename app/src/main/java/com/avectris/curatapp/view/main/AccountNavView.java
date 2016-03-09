package com.avectris.curatapp.view.main;

import com.avectris.curatapp.view.base.MvpView;
import com.avectris.curatapp.vo.Account;

import java.util.List;

/**
 * Created by thuongle on 2/13/16.
 */
interface AccountNavView extends MvpView {

    void onAccountsReturn(List<Account> accounts);

    void onNoAccountReturn();

    void onError(String message);

    void onNoAccountAfterDelete();
}
