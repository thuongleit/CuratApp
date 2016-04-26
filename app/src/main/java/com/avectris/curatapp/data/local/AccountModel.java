package com.avectris.curatapp.data.local;

import com.avectris.curatapp.vo.Account;
import com.avectris.curatapp.vo.Account_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

import java.util.List;

/**
 * Created by thuongle on 2/13/16.
 */
@Singleton
public class AccountModel extends BaseModel<Account> {

    @Inject
    public AccountModel() {
    }

    @Override
    protected Class<Account> clazz() {
        return Account.class;
    }

    public boolean updateActiveAccount(Account account) {
        //if active state, need to deactivate other accounts
        List<Account> accounts = loadAll();
        if (accounts != null && !accounts.isEmpty()) {
            for (Account accountInDb : accounts) {
                if (accountInDb.id.equals(account.id)) {
                    accountInDb.current = true;
                } else {
                    accountInDb.current = false;
                }
                accountInDb.update();
            }
        }
        return true;
    }

    public Account getAccountById(String id) {
        return SQLite
                .select()
                .from(clazz())
                .where(Account_Table.id.eq(id))
                .querySingle();
    }

    public Account getCurrentAccount() {
        return SQLite
                .select()
                .from(clazz())
                .where(Account_Table.current.eq(true))
                .querySingle();
    }

    public Account getAccountByApiCode(String apiCode) {
        return SQLite
                .select()
                .from(clazz())
                .where(Account_Table.apiCode.eq(apiCode))
                .querySingle();
    }
}
