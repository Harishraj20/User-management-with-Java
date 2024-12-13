package com.task.Controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.Model.User;
import com.task.Service.UserService;

public class UserControllerTest {

    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    private User testUser;
    private User loginUser;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService);
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        testUser = new User("Arvind Kumar", "Aravind@1", "arvind.kumar@gmail.com",
                "1992-12-10", "Software Engineer", "Admin", 1, "Male");

        loginUser = new User("Siva", "siva123", "siva@gmail.com", "1990-01-01", "Developer", "Admin", 1,
                "Male");
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

        verify(userService, times(1)).authenticateUser(eq("Harish@gmail.com"), eq("Harish@1"), any(HttpSession.class));
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
                .andExpect(flash().attributeExists("message"));

        verify(userService, times(1)).authenticateUser(eq("Harish@gmail.com"), eq("Harish@1"), any(HttpSession.class));
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

        verify(userService, times(1)).updateUserPassword(any(HttpSession.class), eq("Harish@1"), eq("Harish@12"));
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

        verify(userService, times(1)).updateUserPassword(any(HttpSession.class), eq("Harish@1"), eq("Harish@12"));
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

        verify(userService, times(1))
                .prepareUserPage(eq(1), eq(10), any(HttpSession.class), any(Model.class));
    }

    @Test
    public void testViewInfos_Authenticated() throws Exception {

        mockMvc.perform(get("/users/viewInfo/1")
                .sessionAttr("LoginUser", "admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("LoginInfo"));

        verify(userService, times(1)).prepareLoginInfoPage(eq("1"), eq(1), eq(10), any(Model.class));
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

    @Test
    public void testViewSearchPage() throws Exception {
        mockMvc.perform(get("/users/search").sessionAttr("LoginUser", "admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("searchPage"));
    }

    @Test
    public void testSearchResults() throws Exception {

        List<User> mockUsers = Arrays.asList(loginUser, testUser);

        when(userService.getBySearch("john")).thenReturn(mockUsers);
        mockMvc.perform(MockMvcRequestBuilders.get("/users/searchResults/john").accept(MediaType.APPLICATION_JSON_UTF8)).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)); }

}
