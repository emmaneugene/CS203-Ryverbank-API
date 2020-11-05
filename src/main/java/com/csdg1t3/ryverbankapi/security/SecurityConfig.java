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

    /**
     * Configure SecurityConfig with AuthenticationManagerBuilder
     * @param auth The AuthenticationManagerBuilder to be used.
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
        .userDetailsService(userDetailsService)
        .passwordEncoder(encoder());
    }

    /**
     * Defines which role is able to access specific URLs
     * @param http The HTTP Security
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .httpBasic()
            .and()
        .authorizeRequests()
            // customers 
            .antMatchers(HttpMethod.GET, "/api/customers").hasRole("MANAGER")
            .antMatchers(HttpMethod.GET, "/api/customers/*").hasAnyRole("MANAGER", "USER")
            .antMatchers(HttpMethod.POST, "/api/customers").hasRole("MANAGER")
            .antMatchers(HttpMethod.PUT, "/api/customers/*").hasAnyRole("MANAGER", "USER")
            // accounts 
            .antMatchers(HttpMethod.GET, "/api/accounts/*").hasRole( "USER")
            .antMatchers(HttpMethod.POST, "/api/accounts/*/transactions").hasRole("USER")
            .antMatchers(HttpMethod.POST, "/api/accounts").hasRole("MANAGER")
            // contents
            .antMatchers(HttpMethod.GET, "/api/contents/*").hasAnyRole("USER", "MANAGER", "ANALYST")
            .antMatchers(HttpMethod.POST, "/api/contents").hasAnyRole("MANAGER", "ANALYST")
            .antMatchers(HttpMethod.PUT, "/api/contents/*").hasAnyRole("MANAGER", "ANALYST")
            .antMatchers(HttpMethod.DELETE, "/api/contents/*").hasAnyRole("MANAGER", "ANALYST")
            // stocks
            .antMatchers(HttpMethod.GET, "/api/stocks", "/api/stocks/*").hasRole("USER")
            // trades
            .antMatchers(HttpMethod.GET, "/api/trades", "/api/trades/*").hasRole("USER")
            .antMatchers(HttpMethod.POST, "/api/trades").hasRole("USER")
            .antMatchers(HttpMethod.PUT, "/api/trades/*").hasRole("USER")
            // portfolio
            .antMatchers(HttpMethod.GET, "/api/portfolio").hasRole("USER")
            // reset
            .antMatchers(HttpMethod.PUT, "/api/reset").hasRole("MANAGER")            
            .and()
        .csrf().disable() // CSRF protection is needed only for browser based attacks
        .formLogin().disable()
        .headers().disable(); // Disable the security headers, as we do not return HTML in our service
    }

    /**
     * Calls to encoder will return the BCryptPasswordEncoder,
     * which hashes what is given to it.
     * 
     * For our use case, it would be used to hash plaintext passwords.
     */
    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
