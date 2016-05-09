package com.avectris.curatapp.data.local.migration;

import com.avectris.curatapp.data.local.CuratAppDatabase;
import com.avectris.curatapp.vo.Account;
import com.avectris.curatapp.vo.Account_Table;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import timber.log.Timber;

/**
 * Created by thuongle on 4/25/16.
 */
@Migration(database = CuratAppDatabase.class, version = 3)
public class MigrationVersion3 extends AlterTableMigration<Account> {

    public MigrationVersion3() {
        super(Account.class);
    }

    @Override
    public void onPreMigrate() {
        Timber.d("Migrate db version to 3");
        addColumn(SQLiteType.TEXT, Account_Table.userEmail.getNameAlias().getName());
    }
}
