package com.task.Security;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class LogoutHandlerCookie implements LogoutHandler {

    protected static final Logger logger = LogManager.getLogger();

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logger.info("Inside logout handler to delete cookies");

        logger.info("Requested URI is:{} with the method: {} ", request.getRequestURI(), request.getMethod());

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/finaltask"); 
                    response.addCookie(cookie);
                    logger.info("Cleared jwtToken cookie");
                }
            }
        }

        logger.info("Session invalidated");
    }
}
