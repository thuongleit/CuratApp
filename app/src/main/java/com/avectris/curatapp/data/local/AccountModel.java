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
                        Account_Table.id.eq(account.getId()),
                        Account_Table.name.eq(account.getName()),
                        Account_Table.active.eq(account.getActive()),
                        Account_Table.apiCode.eq(account.getApiCode()));

        Timber.i(insertQuery.getQuery());

        insertQuery.execute();
    }
}
