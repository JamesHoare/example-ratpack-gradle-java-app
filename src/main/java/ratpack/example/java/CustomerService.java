package ratpack.example.java;

import java.util.List;

/**
 * An example service interface.
 *
 * @see CustomerHandler
 */
public interface CustomerService {


    public List<Customer> getCustomers();

    public Customer getCustomer(String id);

    public Customer createCustomer(String name, String email);

    public Customer updateCustomer(String id, String name, String email);


}
