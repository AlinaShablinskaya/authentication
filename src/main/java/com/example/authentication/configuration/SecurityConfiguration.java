package com.example.authentication.configuration;

import com.example.authentication.jwt.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.authentication.configuration.constants.UrlConstant.URL_TO_FORGOT_PASSWORD;
import static com.example.authentication.configuration.constants.UrlConstant.URL_TO_GENERATE_TOKEN;
import static com.example.authentication.configuration.constants.UrlConstant.URL_TO_LOGIN;
import static com.example.authentication.configuration.constants.UrlConstant.URL_TO_RECOVER_PASSWORD;
import static com.example.authentication.configuration.constants.UrlConstant.URL_TO_REGISTRATION;
import static com.example.authentication.configuration.constants.UrlConstant.URL_TO_VERIFY_USER;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final JwtTokenFilter jwtFilter;
    private static final String[] AUTH_WHITELIST = {URL_TO_REGISTRATION, URL_TO_LOGIN, URL_TO_GENERATE_TOKEN,
            URL_TO_VERIFY_USER, URL_TO_FORGOT_PASSWORD, URL_TO_RECOVER_PASSWORD};

    public SecurityConfiguration(JwtTokenFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(AUTH_WHITELIST).access("permitAll()")
                .anyRequest()
                .authenticated()
                .and()
                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }

    @Bean
    public PasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder(12);
    }
}
