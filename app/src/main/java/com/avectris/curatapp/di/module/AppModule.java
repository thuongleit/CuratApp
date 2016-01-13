package com.avectris.curatapp.di.module;

import android.content.Context;

import com.avectris.curatapp.App;
import com.avectris.curatapp.config.Config;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by thuongle on 1/13/16.
 */
@Module
public class AppModule {
    private final App mApp;

    public AppModule(App mApp) {
        this.mApp = mApp;
    }

    @Singleton
    @Provides
    public Context appContext() {
        return mApp;
    }

    @Singleton
    @Provides
    public Config config() {
        return new Config(mApp);
    }
}
