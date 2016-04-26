package com.avectris.curatapp.data.local;

import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * Created by thuongle on 2/13/16.
 */
public abstract class BaseModel<T extends com.raizlabs.android.dbflow.structure.BaseModel> {

    public BaseModel() {
    }

    public List<T> loadAll() {
        return SQLite
                .select()
                .from(clazz())
                .queryList();
    }

    public void save(List<T> objects) {
        if (objects != null && !objects.isEmpty()) {
            TransactionManager.transact(CuratAppDatabase.DATABASE_NAME, () -> {
                for (T object : objects) {
                    object.save();
                }
            });
        }
    }

    public void deleteAll() {
        List<T> objects = loadAll();
        TransactionManager.transact(CuratAppDatabase.DATABASE_NAME, () -> {
            for (T object : objects) {
                object.delete();
            }
        });
    }

    public void save(T object) {
        if (object != null) {
            object.save();
        }
    }

    public void update(T object) {
        if (object != null) {
            object.update();
        }
    }

    protected abstract Class<T> clazz();

}
