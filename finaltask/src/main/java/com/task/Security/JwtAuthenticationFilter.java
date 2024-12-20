package com.task.Security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.task.Service.MyUserDetailsService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MyUserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, MyUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    private static final Logger logger = LogManager.getLogger();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("Inside doFilterInternal method");

        String requestURI = request.getRequestURI();
        logger.info("Request URI: {}", requestURI);

        if (requestURI.equals("/finaltask/login")
                || requestURI.startsWith("/finaltask/css/")
                || requestURI.startsWith("/finaltask/javascript/")) {
            logger.info("Skipping JWT filter for: " + requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        logger.info("Requested URI By the Client: {}", requestURI);
        String token = null;
        String username = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    logger.info("Token retrieved is:{}", token);
                    break;
                }
            }
        }   

        if (token != null) {
            logger.info("Token found in cookies, validating token...");

            try {
                username = jwtService.extractUsername(token);
            } catch (Exception e) {
                logger.error("Failed to extract username from token: {}", e.getMessage());
            }
        } else {
            logger.info("No JWT token found in cookies");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { //checks user is not authenticated previously -- To prevent authentication of already authenticated path
            logger.info("Valid token, attempting authentication for user: {}", username);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                logger.warn("Invalid token for user: {}", username);
            }
        }

        filterChain.doFilter(request, response);
    }

}
