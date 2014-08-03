package ratpack.example.java;

import ratpack.codahale.metrics.CodaHaleMetricsModule;
import ratpack.guice.ModuleRegistry;
import ratpack.h2.H2Module;
import ratpack.handlebars.HandlebarsModule;
import ratpack.handling.Chain;
import ratpack.handling.ChainAction;
import ratpack.handling.Handler;
import ratpack.hikari.HikariModule;
import ratpack.jackson.JacksonModule;
import ratpack.launch.LaunchConfig;

import java.util.Map;

import static ratpack.guice.Guice.handler;
import static ratpack.jackson.Jackson.json;

public class HandlerFactory implements ratpack.launch.HandlerFactory {

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
    private void registerModules(ModuleRegistry moduleRegistry) {
        moduleRegistry.register(new CodaHaleMetricsModule());
        moduleRegistry.register(new MyModule());
        moduleRegistry.register(new JacksonModule());
        moduleRegistry.register(new H2Module());
        moduleRegistry.register(new HikariModule());
        moduleRegistry.register(new HandlebarsModule());
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
            // Map to /foo
            handler("foo", context -> context.render("from the foo handler"));

            // Map to /bar
            handler("bar", context -> context.render("from the bar handler"));

            // Map to /james
            handler("james", context -> context.render("from the james handler"));


            // Set up a nested routing block, which is delegated to `nestedHandler`
            prefix("nested", this::nestedHandler);

            // Map to a dependency injected handler
            handler("injected", getRegistry().get(MyHandler.class));

            // Bind the /static app path to the src/ratpack/assets/images dir
            // Try /static/logo.png
            prefix("static", (Chain nested) -> nested.assets("assets/images"));

            // If nothing above matched, we'll get to here.
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

        private class Customer {

            private String firstName;
            private String lastName;

            private Customer(String firstName, String lastName) {
                this.firstName = firstName;
                this.lastName = lastName;
            }


            public String getLastName() {
                return lastName;
            }

            public String getFirstName() {
                return firstName;
            }
        }
    }
}




