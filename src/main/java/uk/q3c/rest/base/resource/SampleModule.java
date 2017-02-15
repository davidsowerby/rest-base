package uk.q3c.rest.base.resource;

import com.google.inject.AbstractModule;

public class SampleModule extends AbstractModule {

    @Override
    protected void configure() {

        bind(SampleResource.class);
    }
}