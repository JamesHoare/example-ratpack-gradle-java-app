package ratpack.example.java;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ratpack.codahale.metrics.CodaHaleMetricsModule;
import ratpack.codahale.metrics.HealthCheckHandler;
import ratpack.guice.BindingsSpec;
import ratpack.h2.H2Module;
import ratpack.handlebars.HandlebarsModule;
import ratpack.handling.Chain;
import ratpack.handling.ChainAction;
import ratpack.handling.Handler;
import ratpack.hikari.HikariModule;
import ratpack.jackson.JacksonModule;
import ratpack.launch.LaunchConfig;

import java.util.List;
import java.util.Map;

import static ratpack.guice.Guice.handler;
import static ratpack.jackson.Jackson.json;

public class HandlerFactory implements ratpack.launch.HandlerFactory {


    final Logger log = LoggerFactory.getLogger(HandlerFactory.class);

    @Override
    public Handler create(LaunchConfig launchConfig) throws Exception {
        // A Handler that makes objects bound to Guice by modules available downstream
        return handler(launchConfig, this::registerModules, new Routes());
    }

    /**
     * Registers all of the Guice modules that make up the application.
     * <p>
     * This is only invoked once during application bootstrap. If you change the
     * module configuration of an application, you must restart it.
     */
    private void registerModules(BindingsSpec bindingsSpec) {

        Map dataSourceProperties = ImmutableMap.of("URL", "jdbc:h2:mem:dev");
        List<Module> modules = ImmutableList.of((new CodaHaleMetricsModule().healthChecks().jmx().jvmMetrics().metrics()),
                new CustomerModule(), new JacksonModule(), new H2Module(), new HikariModule(dataSourceProperties, "org.h2.jdbcx.JdbcDataSource"), new HandlebarsModule());
        bindingsSpec.add(modules);


    }

    private class Routes extends ChainAction {
        /**
         * Adds potential routes.
         * <p>
         * After this method completes, a handler chain will be constructed from
         * the specified routes.
         * <p>
         * This method will be called for every request. This makes it possible
         * to dynamically define the routes if necessary.
         */
        protected void execute() throws Exception {

            // Map to /customer
            handler("customer", context -> context.render(json(new Customer("james", "hoare"))));


            // Set up a nested routing block, which is delegated to `nestedHandler`
            prefix("nested", this::nestedHandler);

            // Map to a dependency injected handler
            handler("customerhandler", getRegistry().get(CustomerHandler.class));

            // Bind the /static app path to the src/ratpack/assets/images dir
            // Try /static/logo.png
            prefix("static", (Chain nested) -> nested.assets("assets/images"));


            handler("health-check/:name?", context -> context.render(json(new HealthCheckHandler())));

            // default handler
            handler(context -> context.render(json(new Customer("james", "hoare"))));


        }

        private void nestedHandler(Chain nested) {
            // Map to /nested/*/*
            nested.handler(":var1/:var2?", context -> {
                // The path tokens are the :var1 and :var2 path components above
                Map<String, String> pathTokens = context.getPathTokens();
                context.render(
                        "from the nested handler, var1: " + pathTokens.get("var1") +
                                ", var2: " + pathTokens.get("var2")
                );
            });
        }


    }
}




