package com.csdg1t3.ryverbankapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.csdg1t3.ryverbankapi.customer.*;
import com.csdg1t3.ryverbankapi.customer.CustomerRepository;
import com.csdg1t3.ryverbankapi.client.RestTemplateClient;
import com.csdg1t3.ryverbankapi.user.User;
import com.csdg1t3.ryverbankapi.user.UserRepository;
import com.csdg1t3.ryverbankapi.security.*;

@SpringBootApplication
public class RyverbankApiApplication {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(RyverbankApiApplication.class, args);

        CustomerRepository cr = ctx.getBean(CustomerRepository.class);
        Customer tstark = new Customer(1, "Tony Stark", "S8732269I", 80437586, "10880 Malibu Point, 90265", "iamironman", "i<3carmen", "USER", true);
        Customer tholland = new Customer(2, "Tom Holland", "S9847385E", 93580378, "20 Ingram Street", "spiderman", "mrstark,Idontfeels0good", "USER", true);

        System.out.println("[Add customer]: " + cr.save(tstark).getName());
        System.out.println("[Add customer]: " + cr.save(tholland).getName());

        UserRepository users = ctx.getBean(UserRepository.class);
        BCryptPasswordEncoder encoder = ctx.getBean(BCryptPasswordEncoder.class);
        System.out.println("[Add user]: " + users.save(new User("admin", encoder.encode("goodpassword"), "ADMIN")).getUsername());

        // RestTemplateClient client = ctx.getBean(RestTemplateClient.class);
        // Customer cx = new Customer(3, "Carmmie Yip", "S9984627E", 93749560, "66 Lorong 4 Toa Payoh #01-317 S310066", "potatoes", "p0tatoes<3", "USER", true);
        // System.out.println("[Add customer]: " + client.addCustomer("http://localhost:8080/customers", cx));

        // System.out.println("[Get customer]: " + client.getCustomerEntity("http://localhost:8080/customers", 1L).getBody().getName());
    }
}
