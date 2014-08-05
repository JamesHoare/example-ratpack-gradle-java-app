package ratpack.example.java;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * The service implementation.
 *
 * @see CustomerHandler
 */
public class CustomerServiceImpl implements CustomerService {


    private static Map<String, Customer> customers = new ImmutableMap.Builder<String, Customer>()
            .put("1", new Customer("james", "james.hoare@net-a-porter.com"))
            .build();


    public List<Customer> getCustomers() {
        return customers.values().stream().collect(toList());
    }

    public Customer getCustomer(String id) {
        return Optional.ofNullable(customers.get(id)).orElseThrow(() -> new NoSuchElementException("Customer could not be found for id: " + id));
    }

    public Customer createCustomer(String name, String email) {
        Customer customer = new Customer(name, email);
        customers.put(customer.getId(), customer);
        return customer;
    }

    public Customer updateCustomer(String id, String name, String email) {
        Optional<Customer> customer = Optional.ofNullable(customers.get(id));
        customer.ifPresent(c -> c.setName(name));
        customer.ifPresent(c -> c.setEmail(email));
        return customer.orElseThrow(() -> new NoSuchElementException("Customer could not be updated for id: " + id));
    }


}
