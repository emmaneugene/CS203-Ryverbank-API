package com.csdg1t3.ryverbankapi;

import java.util.*;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.csdg1t3.ryverbankapi.client.RestTemplateClient;
import com.csdg1t3.ryverbankapi.content.*;
import com.csdg1t3.ryverbankapi.user.*;
import com.csdg1t3.ryverbankapi.security.*;
import com.csdg1t3.ryverbankapi.trade.*;

import java.io.File;

@SpringBootApplication
public class RyverbankApiApplication {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(RyverbankApiApplication.class, args);

        UserRepository users = ctx.getBean(UserRepository.class);
        ContentRepository contents = ctx.getBean(ContentRepository.class);
        BCryptPasswordEncoder encoder = ctx.getBean(BCryptPasswordEncoder.class);
        PortfolioRepository portfolios = ctx.getBean(PortfolioRepository.class);
        StockController stockController = ctx.getBean(StockController.class);

        User admin = new User(1, "admin", "S1234567G", "80756529", "Lalaland 10, Potato's Dream, 10200", "manager_1", encoder.encode("01_manager_01"), "ROLE_MANAGER", true);
        User tstark = new User(2, "Tony Stark", "S8732269I", "80437586", "10880 Malibu Point, 90265", "iamironman", encoder.encode("i<3carmen"), "ROLE_USER", true);
        User tholland = new User(3, "Tom Holland", "S9847385E", "93580378", "20 Ingram Street", "spiderman", encoder.encode("mrstark,Idontfeels0good"), "ROLE_USER", true);
        User analyst1 = new User(4, "analyst 1", "S1234567D", "00000000", "no address", "analyst_1", encoder.encode("01_analyst_01"), "ROLE_ANALYST", true);
        User analyst2 = new User(5, "analyst 2", "S1234567D", "00000000", "no address", "analyst_2", encoder.encode("02_analyst_02"), "ROLE_ANALYST", true);

        // Add users
        System.out.println("[Add manager]: " + users.save(admin));
        System.out.println("[Add customer]: " + users.save(tstark));
        System.out.println("[Add customer]: " + users.save(tholland));
        System.out.println("[Add analyst]: " + users.save(analyst1));
        System.out.println("[Add analyst]: " + users.save(analyst2));

        // Add portfolios
        portfolios.save(new Portfolio(tstark.getId(), tstark, null, 0, 0));
        portfolios.save(new Portfolio(tholland.getId(), tholland, null, 0, 0));
        
        // Add content
        contents.save(new Content(1, "Title1", "Summary1", "Content1", "Link1", true));
        contents.save(new Content(2, "Title2", "Summary2", "Content2", "Link2", false));
        
        // Add stocks and market maker trades
        stockController.createStocks();
        

        // RestTemplateClient client = ctx.getBean(RestTemplateClient.class);
        // User cx = new User(4, "Carmmie Yip", "S9984627E", 93749560, "66 Lorong 4 Toa Payoh #01-317 S310066", "potatoes", encoder.encode("p0tatoes<3"),  "MANAGER", true);
        // System.out.println("[Add customer, manager]: " + client.addUser("http://localhost:8080/customers", cx));

        // System.out.println("[Get customer]: " + client.getUserEntity("http://localhost:8080/customers", 1L).getBody().getUsername());
    }
}
