package com.csdg1t3.ryverbankapi;

import java.util.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.csdg1t3.ryverbankapi.client.RestTemplateClient;
import com.csdg1t3.ryverbankapi.user.*;
import com.csdg1t3.ryverbankapi.security.*;

@SpringBootApplication
public class RyverbankApiApplication {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(RyverbankApiApplication.class, args);

        UserRepository users = ctx.getBean(UserRepository.class);
        BCryptPasswordEncoder encoder = ctx.getBean(BCryptPasswordEncoder.class);

        User admin = new User(1, "admin", "S1234567G", 80756529, "Lalaland 10, Potato's Dream, 10200", "manager_1", encoder.encode("01_manager_01"), new String[]{"ROLE_MANAGER"}, true);
        User tstark = new User(2, "Tony Stark", "S8732269I", 80437586, "10880 Malibu Point, 90265", "iamironman", encoder.encode("i<3carmen"), new String[]{"ROLE_USER"}, true);
        User tholland = new User(3, "Tom Holland", "S9847385E", 93580378, "20 Ingram Street", "spiderman", encoder.encode("mrstark,Idontfeels0good"), new String[]{"ROLE_USER"}, true);

        System.out.println("[Add manager]: " + users.save(admin));
        System.out.println("[Add customer]: " + users.save(tstark));
        System.out.println("[Add customer]: " + users.save(tholland));

        // RestTemplateClient client = ctx.getBean(RestTemplateClient.class);
        // User cx = new User(4, "Carmmie Yip", "S9984627E", 93749560, "66 Lorong 4 Toa Payoh #01-317 S310066", "potatoes", encoder.encode("p0tatoes<3"),  "MANAGER", true);
        // System.out.println("[Add customer, manager]: " + client.addUser("http://localhost:8080/customers", cx));

        // System.out.println("[Get customer]: " + client.getUserEntity("http://localhost:8080/customers", 1L).getBody().getUsername());
    }
}
