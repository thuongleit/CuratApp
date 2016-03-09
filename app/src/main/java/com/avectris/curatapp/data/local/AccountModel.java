package com.avectris.curatapp.data.local;

import com.avectris.curatapp.vo.Account;
import com.avectris.curatapp.vo.Account_Table;
import com.raizlabs.android.dbflow.sql.language.Insert;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
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

    public void saveOrUpdate(Account account) {
        Insert<? extends com.raizlabs.android.dbflow.structure.BaseModel> insertQuery = SQLite
                .insert(getModelClazz())
                .orReplace()
                .columnValues(
                        Account_Table.accountId.eq(account.getAccountId()),
                        Account_Table.name.eq(account.getName()),
                        Account_Table.active.eq(account.getActive()),
                        Account_Table.apiCode.eq(account.getApiCode()),
                        Account_Table.current.eq(account.isCurrentAccount()),
                        Account_Table.enable_notification.eq(account.isEnableNotification()));

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

    public Observable<Boolean> delete(long accountId) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    Where<? extends com.raizlabs.android.dbflow.structure.BaseModel> query = SQLite
                            .delete(getModelClazz())
                            .where(Account_Table.accountId.eq(accountId));

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
        SQLite
                .update(getModelClazz())
                .orIgnore()
                .set(Account_Table.current.eq(isCurrent))
                .where(Account_Table.accountId.eq(account.getAccountId()))
                .execute();
    }

    public void updatePushNotification(Account account, boolean notification) {
        SQLite
                .update(getModelClazz())
                .orIgnore()
                .set(Account_Table.enable_notification.eq(notification))
                .where(Account_Table.accountId.eq(account.getAccountId()))
                .execute();
    }
}
