package com.avectris.curatapp.data.local;

import com.avectris.curatapp.vo.User;
import com.avectris.curatapp.vo.User_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by thuongle on 4/25/16.
 */
@Singleton
public class UserModel extends BaseModel<User> {

    @Inject
    public UserModel() {

    }

    @Override
    protected Class<User> clazz() {
        return User.class;
    }

    public User getActiveUser() {
        return SQLite
                .select()
                .from(clazz())
                .where(User_Table.isActive.eq(true))
                .querySingle();
    }
}
