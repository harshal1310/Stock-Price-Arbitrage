package com.broker.arbitrage.Security;


import com.broker.arbitrage.Entity.Type.Roles;
import com.broker.arbitrage.Entity.User;
import com.broker.arbitrage.Filters.JWTFilterChain;
import com.broker.arbitrage.Filters.LoggingFilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class AuthSecurity {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JWTFilterChain jwtFilterChain, LoggingFilterChain loggingFilterChain, HandlerExceptionResolver handlerExceptionResolver) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/signup", "/auth/login", "/auth/create-admin", "/prices-stream").permitAll()
                        .requestMatchers("/admin/**").hasRole(Roles.Admin.name())
                        .requestMatchers( "/api/auth/**").hasAnyRole(Roles.User.name(), Roles.Admin.name())
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> {})

                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilterChain, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(loggingFilterChain, JWTFilterChain.class)
                .logout(logout -> logout.permitAll())

                // ---------------------------------------------
                //  CUSTOM EXCEPTION HANDLING
                // ---------------------------------------------
                .exceptionHandling(exception -> exception
                        // When request has no auth or invalid token → 401
                        .authenticationEntryPoint((request, response, authException) -> {
                            handlerExceptionResolver.resolveException(
                                    request, response, null, authException
                            );
                        })

                        // When user is authenticated but not allowed → 403
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            handlerExceptionResolver.resolveException(
                                    request, response, null, accessDeniedException
                            );
                        })
                );

        http.exceptionHandling(exception -> exception
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    Map<String, Object> data = new HashMap<>();
                    data.put("status", 403);
                    data.put("error", "Forbidden");
                    data.put("message", "You do not have permission to access this resource");
                    data.put("path", request.getRequestURI());
                    new ObjectMapper().writeValue(response.getOutputStream(), data);
                })
        );


        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        // Respond with 401 Unauthorized instead of redirecting to login page
        return new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
    }



}
