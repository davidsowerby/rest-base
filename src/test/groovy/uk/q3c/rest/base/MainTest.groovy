package uk.q3c.rest.base

import org.jboss.resteasy.client.jaxrs.ResteasyClient
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget
import spock.lang.Specification

import javax.ws.rs.core.UriBuilder

/**
 * Created by David Sowerby on 14 Feb 2017
 */
class MainTest extends Specification {

    static class MainRunner implements Runnable {

        @Override
        void run() {
            Main.main()
        }
    }

    static Thread runnerThread
//    static class TestModule extends AbstractModule {
//
//        @Override
//        protected void configure() {
//            bind(MessageResource2)
//        }
//    }
//
//    static class TestApplication implements RestApplication {
//
//        @Override
//        List<Module> modules() {
//            return ImmutableList.of(new TestModule())
//        }
//    }

    def setupSpec() {
        runnerThread = new Thread(new MainRunner())
        runnerThread.start()
        Thread.sleep(6000)
    }

    def cleanupSpec() {
        runnerThread.interrupt()
    }


    def setup() {

    }

    def "sample application runs"() {
        given:
        final String path = "http://localhost:8080/api/hello/welcome";
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(UriBuilder.fromPath(path));


        when:
        String response = target.request().get(String.class);

        then:
        response == "Hello from wiggly blip  welcome!"
    }
}
