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
            // trades
            // portfolio
            
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
