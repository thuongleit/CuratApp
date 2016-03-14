package com.avectris.curatapp.di.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by thuongle on 1/22/16.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationScope {
}
