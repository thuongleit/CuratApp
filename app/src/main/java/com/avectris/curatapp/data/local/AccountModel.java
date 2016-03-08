package com.avectris.curatapp.data.local;

import com.avectris.curatapp.vo.Account;
import com.avectris.curatapp.vo.Account_Table;
import com.raizlabs.android.dbflow.sql.language.Insert;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by thuongle on 2/13/16.
 */
public class AccountModel extends BaseModel {

    @Inject
    public AccountModel() {
    }

    @Override
    public Class<? extends com.raizlabs.android.dbflow.structure.BaseModel> getModelClazz() {
        return Account.class;
    }

    public void save(Account account) {
        Insert<? extends com.raizlabs.android.dbflow.structure.BaseModel> insertQuery = SQLite
                .insert(getModelClazz())
                .orReplace()
                .columnValues(
                        Account_Table.accountId.eq(account.getAccountId()),
                        Account_Table.name.eq(account.getName()),
                        Account_Table.active.eq(account.getActive()),
                        Account_Table.apiCode.eq(account.getApiCode()),
                        Account_Table.current.eq(account.isCurrentAccount()));

        Timber.i(insertQuery.getQuery());

        insertQuery.execute();
    }

    public void updateToken(String token) {
        SQLite
                .update(getModelClazz())
                .orFail()
                .set(Account_Table.gcm_token.eq(token))
                .execute();
    }
}
