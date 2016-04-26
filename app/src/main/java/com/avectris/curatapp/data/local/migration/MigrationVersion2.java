package com.avectris.curatapp.data.local.migration;

import com.avectris.curatapp.data.local.CuratAppDatabase;
import com.avectris.curatapp.vo.Account_Adapter;
import com.avectris.curatapp.vo.Account_Table;
import com.avectris.curatapp.vo.User_Adapter;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import timber.log.Timber;

/**
 * Created by thuongle on 4/25/16.
 */
@Migration(database = CuratAppDatabase.class, version = 2)
public class MigrationVersion2 extends BaseMigration {

    @Override
    public void migrate(DatabaseWrapper database) {
        Timber.d("Migrate db version to 2");

        Account_Adapter accountAdapter = new Account_Adapter(null);
        database.execSQL("DROP TABLE IF EXISTS " + accountAdapter.getTableName());
        database.execSQL(accountAdapter.getCreationQuery());

        User_Adapter userAdapter = new User_Adapter(null);
        database.execSQL(userAdapter.getCreationQuery());
    }
}
