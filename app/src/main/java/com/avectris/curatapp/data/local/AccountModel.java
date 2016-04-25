package com.avectris.curatapp.data.local;

import com.avectris.curatapp.vo.Account;
import com.avectris.curatapp.vo.Account_Table;
import com.raizlabs.android.dbflow.sql.language.Insert;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

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

    public void saveOrUpdate(Account account) {
        deactiveAccounts();
        Insert<? extends com.raizlabs.android.dbflow.structure.BaseModel> insertQuery = SQLite
                .insert(clazz())
                .orReplace()
                .columnValues(
                        Account_Table.id.eq(account.id),
                        Account_Table.name.eq(account.name),
                        Account_Table.active.eq(account.active),
                        Account_Table.apiCode.eq(account.apiCode),
                        Account_Table.current.eq(account.current),
                        Account_Table.enable_notification.eq(account.enableNotification));

        Timber.i(insertQuery.getQuery());

        insertQuery.execute();
    }

    public void updateToken(Account account, String token) {
        SQLite
                .update(clazz())
                .orFail()
                .set(Account_Table.gcm_token.eq(token))
                .where(Account_Table.id.eq(account.id))
                .execute();
    }

    public Observable<Boolean> delete(String id) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    Where<? extends com.raizlabs.android.dbflow.structure.BaseModel> query = SQLite
                            .delete(clazz())
                            .where(Account_Table.id.eq(id));

                    Timber.d(query.toString());
                    query.execute();

                    subscriber.onNext(Boolean.TRUE);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public void updateActiveAccount(Account account, boolean isCurrent) {
        //if active state, need to deactivate other accounts
        if (isCurrent) {
            deactiveAccounts();
        }
        SQLite
                .update(clazz())
                .orIgnore()
                .set(Account_Table.current.eq(isCurrent))
                .where(Account_Table.id.eq(account.id))
                .execute();
    }

    private void deactiveAccounts() {
        List<? extends com.raizlabs.android.dbflow.structure.BaseModel> accounts = loadAll();
        for (com.raizlabs.android.dbflow.structure.BaseModel model : accounts) {
            Account account1 = (Account) model;
            if (account1.current) {
                updateActiveAccount(account1, false);
                break;
            }
        }
    }

    public void updatePushNotification(Account account, boolean notification) {
        SQLite
                .update(clazz())
                .orIgnore()
                .set(Account_Table.enable_notification.eq(notification))
                .where(Account_Table.id.eq(account.id))
                .execute();
    }

    public Account getAccountById(String id) {
        return (Account) SQLite
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
}
