package com.study.todo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers("/api/v1/users/login", "/api/v1/users/signup").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(formLoginConfigurer ->
                        formLoginConfigurer.loginProcessingUrl("/api/v1/users/login")
                                .defaultSuccessUrl("/api/v1/users/home", true)
                                .permitAll()
                )
                .logout(logoutConfigurer -> {
                    logoutConfigurer
                            .logoutUrl("/api/v1/users/logout")
                            .logoutSuccessUrl("/api/v1/users/login")
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                            .permitAll();

                })
                .csrf(csrfConfigurer -> csrfConfigurer.disable());
        return http.build();
    }
}
