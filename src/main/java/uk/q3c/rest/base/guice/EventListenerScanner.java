package uk.q3c.rest.base.guice;

import com.google.inject.Injector;

import javax.inject.Inject;
import java.util.EventListener;

/**
 * Walks through the guice injector bindings, visiting each one that is an EventListener.
 */
public class EventListenerScanner extends Scanner<EventListener> {

    @Inject
    public EventListenerScanner(Injector injector) {
        super(injector, EventListener.class);
    }
}

// https://github.com/gwizard/gwizard/blob/master/gwizard-web/src/main/java/org/gwizard/web/EventListenerScanner.java