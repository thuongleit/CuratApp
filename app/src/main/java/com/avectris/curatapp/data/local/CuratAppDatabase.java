package com.avectris.curatapp.data.local;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by thuongle on 2/13/16.
 */
@Database(name = CuratAppDatabase.DATABASE_NAME, version = CuratAppDatabase.DATABASE_VERSION)
public class CuratAppDatabase {

    public static final String DATABASE_NAME = "curat_db";

    public static final int DATABASE_VERSION = 1;
}
