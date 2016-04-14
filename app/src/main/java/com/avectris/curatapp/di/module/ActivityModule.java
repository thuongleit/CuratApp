package com.avectris.curatapp.di.module;

import android.app.Activity;
import android.content.Context;

import com.avectris.curatapp.di.scope.ActivityScope;
import com.avectris.curatapp.di.scope.PerActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by thuongle on 1/13/16.
 */
@Module
public class ActivityModule {

    private final Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @ActivityScope
    @Provides
    public Context provideContext() {
        return mActivity;
    }

    @PerActivity
    @Provides
    public Activity provideActivity(){
        return mActivity;
    }
}
