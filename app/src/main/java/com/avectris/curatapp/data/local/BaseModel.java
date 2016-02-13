package com.avectris.curatapp.data.local;

import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import rx.Observable;
import timber.log.Timber;

/**
 * Created by thuongle on 2/13/16.
 */
public abstract class BaseModel {

    public BaseModel() {
    }

    public Observable<List<? extends com.raizlabs.android.dbflow.structure.BaseModel>> getAlls() {
        return Observable.just(getAllToList());
    }

    public List<? extends com.raizlabs.android.dbflow.structure.BaseModel> getAllToList() {
        From<? extends com.raizlabs.android.dbflow.structure.BaseModel> selectQuery =
                SQLite
                        .select()
                        .from(getModelClazz());
        Timber.i(selectQuery.getQuery());
        return selectQuery.queryList();
    }

    public abstract Class<? extends com.raizlabs.android.dbflow.structure.BaseModel> getModelClazz();
}
