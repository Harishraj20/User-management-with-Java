package com.task.Filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.task.Filters.AuthenticationFilter;

public class AuthenticationFilterTest {

    private AuthenticationFilter authenticationFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private HttpSession session;

    @Mock
    private FilterConfig filterConfig;

    @Before
    public void setUp() throws ServletException {
        MockitoAnnotations.initMocks(this);
        authenticationFilter = new AuthenticationFilter();
        authenticationFilter.init(filterConfig);
        when(request.getSession()).thenReturn(session);

    }

    @Test
    public void testUserNotAuthenticated() throws IOException, ServletException {
        when(session.getAttribute("LoginUser")).thenReturn(null);
        when(request.getContextPath()).thenReturn("/finaltask");

        authenticationFilter.doFilter(request, response, chain);

        verify(response).sendRedirect("/finaltask/");
        verifyNoInteractions(chain);
    }

    @Test
    public void testUserAuthenticated() throws IOException, ServletException {
        when(session.getAttribute("LoginUser")).thenReturn(new Object());

        authenticationFilter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, never()).sendRedirect("/users");
    }

    @Test
    public void testFilterInitialization() throws ServletException {
        authenticationFilter.init(filterConfig);
    }

    @Test
    public void testFilterDestruction() {
        authenticationFilter.destroy();
    }
}
