package com.avectris.curatapp.di.module;

import android.app.Application;
import android.content.Context;

import com.avectris.curatapp.CuratApp;
import com.avectris.curatapp.config.Config;
import com.avectris.curatapp.di.scope.ApplicationScope;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by thuongle on 1/13/16.
 */
@Module
public class ApplicationModule {
    private final CuratApp mApp;

    public ApplicationModule(CuratApp mApp) {
        this.mApp = mApp;
    }

    @Singleton
    @Provides
    Application provideApplication(){
        return mApp;
    }

    @ApplicationScope
    @Provides
    public Context provideContext() {
        return mApp;
    }

    @Singleton
    @Provides
    public Config config() {
        return new Config(mApp);
    }
}
