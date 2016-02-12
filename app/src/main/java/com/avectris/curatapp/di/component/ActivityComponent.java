package com.avectris.curatapp.di.component;

import android.content.Context;

import com.avectris.curatapp.di.module.ActivityModule;
import com.avectris.curatapp.di.scope.ActivityScope;
import com.avectris.curatapp.di.scope.PerActivity;

import dagger.Component;

/**
 * Created by thuongle on 1/13/16.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    @ActivityScope
    Context context();
}
