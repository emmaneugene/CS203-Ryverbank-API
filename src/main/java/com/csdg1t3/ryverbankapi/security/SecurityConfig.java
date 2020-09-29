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

@EnableWebSecurity
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
            .antMatchers(HttpMethod.GET, "/content", "/stock", "/trade", "/transfer", "/account", "/customer").hasAnyRole("ADMIN", "USER") // Anyone can view
            .antMatchers(HttpMethod.POST, "/content", "/stock", "/trade", "/account", "/customer").hasRole("ADMIN")
            .antMatchers(HttpMethod.POST, "/transfer").hasAnyRole("ADMIN", "USER")
            .antMatchers(HttpMethod.PUT, "/content", "/stock", "/trade", "/account", "/customer").hasRole("ADMIN")
            .antMatchers(HttpMethod.PUT, "/transfer").hasAnyRole("ADMIN", "USER")
            .antMatchers(HttpMethod.DELETE, "/content", "/stock", "/trade", "/transfer", "/account", "/customer").hasRole("ADMIN")
            .and()
        .csrf().disable() // CSRF protection is needed only for browser based attacks
        .formLogin().disable()
        .headers().disable(); // Disable the security headers, as we do not return HTML in our service
    }
}
