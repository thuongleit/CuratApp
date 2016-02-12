package com.avectris.curatapp.di.component;

import android.content.Context;

import com.avectris.curatapp.config.Config;
import com.avectris.curatapp.di.module.ApplicationModule;
import com.avectris.curatapp.di.scope.ApplicationScope;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by thuongle on 1/13/16.
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    @ApplicationScope Context context();

    Config config();
}
