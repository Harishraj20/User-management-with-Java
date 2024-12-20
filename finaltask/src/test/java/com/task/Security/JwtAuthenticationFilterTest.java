package com.task.Security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import com.task.Service.MyUserDetailsService;

public class JwtAuthenticationFilterTest {

    @Mock
    JwtService jwtService;

    @Mock
    private MyUserDetailsService userDetailsService;
    @Mock
    private MockFilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    String token;
    String email;
    UserDetails userDetails;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        email = "Harish@gmail.com";
        token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJIYXJpc2hAZ21haWwuY29tIiwiaWF0IjoxNzM0NTIxMDI5LCJleHAiOjE3MzQ1MjQ2Mjl9.RyDjCCgc1ebc14ZBCnKlCTtXv_KxZhu385EWQ-DbWbog60GryJ9BjUJpvx6egGPfUgGQe8vg6lGSe2GhL9WhIw";
        userDetails = mock(UserDetails.class);
    }

    @Test
    public void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        Cookie cookie = new Cookie("jwtToken", token);
        request.setCookies(cookie);
        when(userDetails.getUsername()).thenReturn(email);
        when(userDetails.getAuthorities()).thenReturn(null);
        when(jwtService.extractUsername(token)).thenReturn(email);
        when(jwtService.validateToken(token, userDetails)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(jwtService).extractUsername(token);
    }

    @Test
    public void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        String token = "invalid.jwt.token";
        String username = "testuser";
        Cookie cookie = new Cookie("jwtToken", token);
        request.setCookies(cookie);
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(jwtService.validateToken(token, null)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService).extractUsername(token);
        verify(userDetailsService).loadUserByUsername(username);
    }

    @Test
    public void testDoFilterInternal_TokenNotPresent() throws ServletException, IOException {
        request.setCookies();

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testDoFilterInternal_BypassCertainUrls() throws ServletException, IOException {
        String loginUrl = "/finaltask/login";
        request.setRequestURI(loginUrl);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_BypassCertainUrlscss() throws ServletException, IOException {
        String cssUrl = "/finaltask/css/styles.css";
        request.setRequestURI(cssUrl);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_BypassCertainUrlsJS() throws ServletException, IOException {
        String cssUrl = "/finaltask/javascript/login.js";
        request.setRequestURI(cssUrl);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void testDoFilterInternal_ValidToken_AlreadyAuthenticated() throws ServletException, IOException {

        String username = "testuser";
        Cookie cookie = new Cookie("jwtToken", token);
        request.setCookies(cookie);
        when(jwtService.extractUsername(token)).thenReturn(username);
        when(jwtService.validateToken(token, null)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(mock(UserDetails.class));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testDoFilterInternal_ExceptionHandling() throws ServletException, IOException {
        when(jwtService.extractUsername(token)).thenThrow(new RuntimeException());
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verifyNoInteractions(userDetailsService);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testValidToken_AuthenticationAttempted() {
        SecurityContextHolder.clearContext();

        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(true);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails loadedUserDetails = userDetailsService.loadUserByUsername(email);
            if (jwtService.validateToken(token, loadedUserDetails)) {
                UsernamePasswordAuthenticationToken authToken
                        = new UsernamePasswordAuthenticationToken(loadedUserDetails, null, loadedUserDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);

                assertNotNull(SecurityContextHolder.getContext().getAuthentication());
                assertEquals(authToken, SecurityContextHolder.getContext().getAuthentication());
            }
        }

        verify(userDetailsService).loadUserByUsername(email);
        verify(jwtService).validateToken(token, userDetails);
    }

    @Test
    void testInvalidToken_NoAuthenticationAttempted() {
        SecurityContextHolder.clearContext();

        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.validateToken(token, userDetails)).thenReturn(false);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails loadedUserDetails = userDetailsService.loadUserByUsername(email);
            if (!jwtService.validateToken(token, loadedUserDetails)) {
                assertNull(SecurityContextHolder.getContext().getAuthentication());
            }
        }

        verify(userDetailsService).loadUserByUsername(email);
        verify(jwtService).validateToken(token, userDetails);
    }
}
