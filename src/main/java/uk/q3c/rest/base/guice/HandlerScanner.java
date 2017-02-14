package uk.q3c.rest.base.guice;

import com.google.inject.Injector;
import org.eclipse.jetty.server.Handler;

import javax.inject.Inject;

/**
 * Walks through the guice injector bindings, visiting each one that is a Handler.
 */
public class HandlerScanner extends Scanner<Handler> {

    @Inject
    public HandlerScanner(Injector injector) {
        super(injector, Handler.class);
    }
}

// https://github.com/gwizard/gwizard/blob/master/gwizard-web/src/main/java/org/gwizard/web/HandlerScanner.java