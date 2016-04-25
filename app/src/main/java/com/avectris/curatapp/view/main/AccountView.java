package com.avectris.curatapp.view.main;

import android.support.v7.widget.SwitchCompat;

import com.avectris.curatapp.view.base.ErrorView;
import com.avectris.curatapp.view.base.MvpView;
import com.avectris.curatapp.vo.Account;

import java.util.List;

/**
 * Created by thuongle on 2/13/16.
 */
interface AccountView extends ErrorView {

    void onAccountsReturn(List<Account> accounts);

    void onEmptyAccounts();

    void reloadActivity(Account account);

    void onEnableDisableNotificationFailed(SwitchCompat view, boolean state);
}
