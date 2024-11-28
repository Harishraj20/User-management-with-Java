package com.task.Controller;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import com.task.Model.User;
import com.task.Service.UserService;

public class CrudControllerTest {

    private CrudController crudController;

    @Mock
    private UserService userService;
    @Mock
    private Model model;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        crudController = new CrudController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(crudController).build();
    }

    @Test
    public void testAddMethod_Success() throws Exception {
        User user = new User();
        user.setUserName("Harish");
        user.setPassword("Harish@1");

        when(userService.addUsers(any(User.class))).thenReturn(true);

        mockMvc.perform(post("/users/add")
                .param("userName", "Harish")
                .param("password", "Harish@1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name("message"));
    }

    @Test
    public void testAddMethod_Failure() throws Exception {
        when(userService.addUsers(any(User.class))).thenReturn(false);

        mockMvc.perform(post("/users/add")
                .param("userName", "Harish")
                .param("password", "Harish@1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/addform"));
    }

    @Test
    public void testDeleteUser_Authenticated() throws Exception {
        mockMvc.perform(post("/users/delete/1")
                .sessionAttr("LoginUser", "admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    public void testUpdateForm() throws Exception {
        User user = new User();
        user.setUserName("Harish");

        when(userService.findUserById(1)).thenReturn(user);

        mockMvc.perform(get("/users/updateform/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("UpdateForm"));
    }

    @Test
    public void testUpdateMethod_Success() throws Exception {
        when(userService.updateUsers(any(User.class), anyInt())).thenReturn(true);

        mockMvc.perform(post("/users/update")
                .param("userName", "Harish")
                .param("refUserID", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("Message"))
                .andExpect(view().name("message"));
    }

    @Test
    public void testUpdateMethod_Failure() throws Exception {
        when(userService.updateUsers(any(User.class), anyInt())).thenReturn(false);

        mockMvc.perform(post("/users/update")
                .param("userName", "Harish")
                .param("refUserID", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/form?userId=1"));
    }

    @Test
    public void testAddForm() throws Exception {
        mockMvc.perform(get("/users/addform"))
                .andExpect(status().isOk())
                .andExpect(view().name("Signup"));
    }

    @Test
    public void testUpdateForm_UserFound() throws Exception {
        User user = new User();
        user.setUserId(1);
        user.setUserName("Harish");

        when(userService.findUserById(1)).thenReturn(user);

        mockMvc.perform(get("/users/updateform/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("UpdateForm"))
                .andExpect(model().attribute("user", user));
    }

    @Test
    public void testUpdateForm_UserNotFound() throws Exception {
        when(userService.findUserById(99)).thenReturn(null);

        mockMvc.perform(get("/users/updateform/10"))
                .andExpect(status().isOk())
                .andExpect(view().name("UpdateForm"));
    }

}
