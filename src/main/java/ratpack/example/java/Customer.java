package ratpack.example.java;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import java.util.UUID;

/**
 * Created by jameshoare on 05/08/2014.
 */
public class Customer {

    private String id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    public Customer(@NotBlank String name, @NotBlank String email) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
