package com.avectris.curatapp.di.component;

import com.avectris.curatapp.di.scope.PerActivity;

import dagger.Component;

/**
 * Created by thuongle on 1/13/16.
 */
@PerActivity
@Component(dependencies = AppComponent.class)
public interface ActivityComponent {

}
