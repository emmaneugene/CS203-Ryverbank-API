package com.csdg1t3.ryverbankapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;  
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.csdg1t3.ryverbankapi.user.*;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userSvc){
        this.userDetailsService = userSvc;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
        throws Exception {
        auth
        .userDetailsService(userDetailsService)
        .passwordEncoder(encoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .httpBasic()
            .and()
        .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/customers", "/accounts").hasRole("MANAGER")
            .antMatchers(HttpMethod.GET, "/customers/**", "/accounts/**").hasAnyRole("MANAGER", "USER")
            .antMatchers(HttpMethod.PUT, "/customers/**", "/accounts/**").hasRole("MANAGER")
            .antMatchers(HttpMethod.DELETE, "/customers/**","/accounts/**").hasRole("MANAGER")
            .antMatchers(HttpMethod.POST, "/customers/**","/accounts/**").hasRole("MANAGER")
            // .antMatchers(HttpMethod.DELETE, "/content", "/stock", "/trade", "/transfer", "/account", "/customer").hasRole("ADMIN")
            // .antMatchers(HttpMethod.GET, "/content", "/stock", "/trade", "/transfer", "/account", "/customer").hasAnyRole("ADMIN", "USER") // Anyone can view
            //.antMatchers(HttpMethod.POST, "/content", "/stock", "/trade", "/account", "/customer").hasRole("ADMIN")
            //.antMatchers(HttpMethod.PUT, "/content", "/stock", "/trade", "/account", "/customer").hasRole("ADMIN")
            .anyRequest().authenticated() 
            .and()
        .csrf().disable() // CSRF protection is needed only for browser based attacks
        .formLogin().disable()
        .headers().disable(); // Disable the security headers, as we do not return HTML in our service
    }

    /**
     * @Bean annotation is used to declare a PasswordEncoder bean in the Spring application context. 
     * Any calls to encoder() will then be intercepted to return the bean instance.
     */
    
    @Bean
    public BCryptPasswordEncoder encoder() {
        // auto-generate a random salt internally
        return new BCryptPasswordEncoder();
    }
}
