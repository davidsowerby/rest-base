package uk.q3c.rest.base;

import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.reflections.Reflections;
import uk.q3c.rest.base.app.RestApplication;
import uk.q3c.rest.base.app.SampleRestApplication;
import uk.q3c.rest.base.guice.EventListenerScanner;
import uk.q3c.rest.base.guice.HandlerScanner;
import uk.q3c.rest.base.jetty.JettyModule;
import uk.q3c.rest.base.resteasy.RestEasyModule;
import uk.q3c.rest.base.swagger.SwaggerModule;

import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"ThrowCaughtLocally", "OverlyBroadCatchBlock"})
@ThreadSafe
public class Main {

    static final String APPLICATION_PATH = "/api";
    static final String CONTEXT_ROOT = "/";

    private final GuiceFilter filter;
    private final EventListenerScanner eventListenerScanner;
    private final HandlerScanner handlerScanner;

    // Guice can work with both javax and guice annotations.
    @Inject
    public Main(GuiceFilter filter, EventListenerScanner eventListenerScanner, HandlerScanner handlerScanner) {
        this.filter = filter;
        this.eventListenerScanner = eventListenerScanner;
        this.handlerScanner = handlerScanner;
    }

    public static void main(String[] args) throws Exception {

        try {
            Log.setLog(new Slf4jLog());
            final Reflections reflections = new Reflections();
            final Set<Class<? extends RestApplication>> apps = reflections.getSubTypesOf(RestApplication.class);

            // Developer has implemented their own
            if (apps.size() > 1) {
                apps.remove(SampleRestApplication.class);
            }

            // We still have more than one after removing SampleApplication
            if (apps.size() > 1) {
                throw new RestApplicationException("More than one RestApplication implementation has been defined - there must be only one", null);
            }

            final RestApplication app = apps.iterator().next().getConstructor().newInstance();
            Log.getLog().info("Starting RestApplication with implementation {}", app.getClass().getName());
            final List<Module> appModules = app.modules();
            final List<Module> baseModules = ImmutableList.of(new JettyModule(), new RestEasyModule(APPLICATION_PATH), new SwaggerModule(APPLICATION_PATH));
            final List<Module> allModules = new ArrayList<>(baseModules);
            allModules.addAll(appModules);
            final Injector injector = Guice.createInjector(allModules);

            injector.getInstance(Main.class).run();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() throws Exception {

        final int port = 8080;
        final Server server = new Server(port);

        // Setup the basic Application "context" at "/".
        // This is also known as the handler tree (in Jetty speak).
        final ServletContextHandler context = new ServletContextHandler(server, CONTEXT_ROOT);

        // Add the GuiceFilter (all requests will be routed through GuiceFilter).
        final FilterHolder filterHolder = new FilterHolder(filter);
        context.addFilter(filterHolder, APPLICATION_PATH + "/*", null);

        // Setup the DefaultServlet at "/".
        final ServletHolder defaultServlet = new ServletHolder(new DefaultServlet());
        context.addServlet(defaultServlet, CONTEXT_ROOT);

        // Set the path to our static (Swagger UI) resources
        final String resourceBasePath = Main.class.getResource("/swagger-ui").toExternalForm();
        context.setResourceBase(resourceBasePath);
        context.setWelcomeFiles(new String[]{"index.html"});

        // Add any Listeners that have been bound, for example, the
        // GuiceResteasyBootstrapServletContextListener which gets bound in the RestEasyModule.

        eventListenerScanner.accept(listener -> {
            context.addEventListener(listener);
        });

        final HandlerCollection handlers = new HandlerCollection();

        // The Application context is currently the server handler, add it to the list.
        handlers.addHandler(server.getHandler());

        // Add any Handlers that have been bound

        handlerScanner.accept(handler -> {
            handlers.addHandler(handler);
        });

        server.setHandler(handlers);
        server.start();
        server.join();
    }
}