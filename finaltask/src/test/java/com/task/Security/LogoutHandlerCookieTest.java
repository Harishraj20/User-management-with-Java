package com.task.Security;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:RequestServlet-servlet.xml")
@WebAppConfiguration
public class LogoutHandlerCookieTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private Cookie jwtCookie;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
        jwtCookie = new Cookie("jwtToken", "testJwtToken");
        jwtCookie.setPath("/finaltask");
    }

    @Test
    public void testContext() {
        assertNotNull(context);
    }

    @Test
    public void testLogout_withJwtCookie() throws Exception {
        Cookie jwtCookie = new Cookie("jwtToken", "your-jwt-token");
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);

        mockMvc.perform(get("/users/logout")
                .cookie(jwtCookie))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/login"));
    }

    @Test
    public void testLogout_withJSessionIdCookie() throws Exception {
        Cookie jsessionidCookie = new Cookie("JSESSIONID", "testSessionId");
        jsessionidCookie.setPath("/");
        jsessionidCookie.setHttpOnly(true);

        mockMvc.perform(get("/users/logout")
                .cookie(jsessionidCookie))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/login"));

        mockMvc.perform(get("/users/logout")
                .cookie(jsessionidCookie))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/login"));
    }
}
