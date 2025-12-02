package com.broker.arbitrage.Filters;

import com.broker.arbitrage.Entity.User;
import com.broker.arbitrage.Service.UserService;
import com.broker.arbitrage.Util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Security;

@Component
@AllArgsConstructor
public class JWTFilterChain extends OncePerRequestFilter {
    JWTUtil jwtUtil;
    UserService userService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       String authHeader = request.getHeader("Authorization");
        String jwt=null,username=null;

       if(authHeader != null && authHeader.startsWith("Bearer")) {

           jwt = authHeader.substring(7);
           try {
               username = jwtUtil.extractUsername(jwt);
           } catch (Exception e) {
               // log it and skip setting authentication
               logger.warn("Invalid JWT token: {}", e.getCause());
               response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
               response.setContentType("application/json");
               response.getWriter().write("{\"status\":401,\"message\":\"Invalid or expired JWT token\"}");
               response.getWriter().flush(); // make sure it’s written
                return;
           }
       }
       if(username != null && SecurityContextHolder.getContext().getAuthentication() == null ) {
           UserDetails userDetails = userService.loadUserByUsername(username);
           if(userDetails != null && jwtUtil.validateToken(jwt, userDetails.getUsername())) {
               UsernamePasswordAuthenticationToken authToken =
                       new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
               authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
               SecurityContextHolder.getContext().setAuthentication(authToken);

           }

       }
        filterChain.doFilter(request, response);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        System.out.println("path: " + path);
        return path.startsWith("/auth/login") || path.startsWith("/auth/signup") || path.startsWith("/api/prices-stream");
    }

}
