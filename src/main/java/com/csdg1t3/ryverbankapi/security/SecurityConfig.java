package com.csdg1t3.ryverbankapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.csdg1t3.ryverbankapi.user.*;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userSvc){
        this.userDetailsService = userSvc;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
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
            // customers 
            .antMatchers(HttpMethod.GET, "/customers").hasRole("MANAGER")
            .antMatchers(HttpMethod.GET, "/customers/*").hasAnyRole("MANAGER", "USER")
            .antMatchers(HttpMethod.POST, "/customers").hasRole("MANAGER")
            .antMatchers(HttpMethod.PUT, "/customers/*").hasAnyRole("MANAGER", "USER")
            // accounts 
            .antMatchers(HttpMethod.GET, "/accounts/*").hasRole( "USER")
            .antMatchers(HttpMethod.POST, "/accounts/*/transactions").hasRole("USER")
            .antMatchers(HttpMethod.POST, "/accounts").hasRole("MANAGER")
            // contents
            .antMatchers(HttpMethod.GET, "/contents/*").hasAnyRole("USER", "MANAGER", "ANALYST")
            .antMatchers(HttpMethod.POST, "/contents").hasAnyRole("MANAGER", "ANALYST")
            .antMatchers(HttpMethod.PUT, "/contents/*").hasAnyRole("MANAGER", "ANALYST")
            .antMatchers(HttpMethod.DELETE, "/contents/*").hasAnyRole("MANAGER", "ANALYST")
            // stocks
            .antMatchers(HttpMethod.GET, "/stocks", "/stocks/*").hasRole("USER")
            // trades
            .antMatchers(HttpMethod.GET, "/trades", "/trades/*").hasRole("USER")
            .antMatchers(HttpMethod.POST, "/trades").hasRole("USER")
            .antMatchers(HttpMethod.PUT, "/trades/*").hasRole("USER")
            // portfolio
            .antMatchers(HttpMethod.GET, "/portfolio").hasRole("USER")
            
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
        return new BCryptPasswordEncoder();
    }
}
