package ratpack.example.java;

import ratpack.handling.Context;
import ratpack.handling.Handler;

import javax.inject.Inject;
import javax.inject.Singleton;

import static ratpack.jackson.Jackson.json;

/**
 * A handler implementation that is created via dependency injection.
 *
 * @see CustomerModule
 * @see ratpack.example.java.HandlerFactory
 */
@Singleton
public class CustomerHandler implements Handler {

    private final CustomerService customerService;

    @Inject
    public CustomerHandler(CustomerService myService) {
        this.customerService = myService;
    }

    @Override
    public void handle(Context context) {
       context.render(json(customerService.getCustomers()));
    }
}
