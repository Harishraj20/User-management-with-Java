package com.task.Controller;

import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import com.task.Service.UserService;

public class UserControllerTest {

    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testHomepage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("Login"));
    }

    @Test
    public void testLoginUser_Success() throws Exception {
        when(userService.authenticateUser(eq("Harish@gmail.com"), eq("Harish@1"), any(HttpSession.class)))
                .thenReturn(true);

        mockMvc.perform(post("/login")
                .param("emailId", "Harish@gmail.com")
                .param("password", "Harish@1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    public void testLoginUser_Failure() throws Exception {
        when(userService.authenticateUser(eq("Harish@gmail.com"), eq("Harish@1"), any(HttpSession.class)))
                .thenReturn(false);

        mockMvc.perform(post("/login")
                .param("emailId", "Harish@gmail.com")
                .param("password", "Harish@1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message", "Invalid Email-Id or password!"));

    }

    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void testChangePassword_Success() throws Exception {
        when(userService.updateUserPassword(any(HttpSession.class), eq("Harish@1"), eq("Harish@12")))
                .thenReturn(true);

        mockMvc.perform(post("/users/changepassword")
                .param("oldPassword", "Harish@1")
                .param("newPassword", "Harish@12"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("Message"))
                .andExpect(model().attribute("Message", "Password Updated Successfully"))
                .andExpect(view().name("Redirect"));
    }

    @Test
    public void testChangePassword_Failure() throws Exception {
        when(userService.updateUserPassword(any(HttpSession.class), eq("oldPassword"), eq("newPassword")))
                .thenReturn(false);

        mockMvc.perform(post("/users/changepassword")
                .param("oldPassword", "Harish@1")
                .param("newPassword", "Harish@12"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name("ChangePassword"));

    }

    @Test
    public void testChangePasswordPage_Authenticated() throws Exception {
        mockMvc.perform(get("/users/changepassword")
                .sessionAttr("LoginUser", "admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("ChangePassword"));
    }

    @Test
    public void testShowUserPage_Authenticated() throws Exception {
        mockMvc.perform(get("/users")
                .param("pageNumber", "1")
                .param("pageSize", "10")
                .sessionAttr("LoginUser", "admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("Details"));
    }

    @Test
    public void testViewInfos_Authenticated() throws Exception {

        mockMvc.perform(get("/users/viewInfo/1")
                .sessionAttr("LoginUser", "admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("LoginInfo"));
    }

    @Test
    public void testViewInactiveUsers() {
        int pageNumber = 1;
        int pageSize = 10;
        String viewName = userController.viewInactiveUsers(model, session, pageNumber, pageSize);
        verify(userService).prepareInactiveUsersPage(pageNumber, pageSize, session, model);
        verifyNoMoreInteractions(userService);
        assertEquals("InactiveUsers", viewName);
    }

}
