package com.task.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import com.task.Model.Login;
import com.task.Model.User;
import com.task.Repository.UserRepository;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User loginUser;
    private User mockUser;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

      
        when(session.getAttribute("LoginUser")).thenReturn(mockUser);

        testUser = new User("Arvind Kumar", "Aravind@1", "arvind.kumar@gmail.com",
                "1992-12-10", "Software Engineer", "Admin", 1, "Male");
        loginUser = new User("Siva", "siva123", "siva@gmail.com", "1990-01-01", "Developer", "Admin", 1,
                "Male");

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(userRepository.checkUserByEmailid(testUser.getEmailId())).thenReturn(testUser);

    }

    @Test
    public void testAddUser_Success() {

        when(userRepository.checkUserByEmailid(testUser.getEmailId())).thenReturn(null);
        when(userRepository.addUserInfo(testUser)).thenReturn(true);

        boolean result = userService.addUsers(testUser);

        assertTrue(result);
        verify(userRepository).addUserInfo(testUser);
    }

    @Test
    public void testAddUser_Failure_ExistingEmail() {

        when(userRepository.checkUserByEmailid(testUser.getEmailId())).thenReturn(new User());

        boolean result = userService.addUsers(testUser);

        assertFalse(result);
        verify(userRepository, never()).addUserInfo(testUser);
    }

    @Test
    public void testUpdateCredentials() {

        when(userRepository.findUser(testUser.getUserId())).thenReturn(testUser);
        doNothing().when(userRepository).updateUser(testUser);
        doNothing().when(userRepository).saveLoginInfo(any(Login.class));

        userService.updateCredentials(testUser);

        verify(userRepository).updateUser(testUser);
        verify(userRepository).saveLoginInfo(any(Login.class));
    }

    @Test
    public void testGetLoginById() {
        int userId = 1;
        int page = 1;
        int pageSize = 10;
        List<Login> logins = Arrays.asList(new Login(), new Login());

        when(userRepository.getLoginInfo(userId, page, pageSize)).thenReturn(logins);

        List<Login> result = userRepository.getLoginInfo(userId, page, pageSize);

        assertEquals(2, result.size());
        verify(userRepository).getLoginInfo(userId, page, pageSize);
    }

    @Test
    public void testGetTotalLoginCount() {
        int userId = 1;
        int loginCount = 5;

        when(userRepository.getTotalLoginCount(userId)).thenReturn(loginCount);

        int result = userRepository.getTotalLoginCount(userId);

        assertEquals(5, result);
        verify(userRepository).getTotalLoginCount(userId);
    }

    @Test
    public void testFetchInactiveUsers() {
        int pageNumber = 1;
        int pageSize = 10;
        List<User> inactiveUsers = Arrays.asList(new User(), new User());

        when(userRepository.findInactiveUsers(anyInt(), anyInt())).thenReturn(inactiveUsers);

        List<User> result = userService.fetchInactiveUsers(pageNumber, pageSize);

        assertEquals(2, result.size());
        verify(userRepository).findInactiveUsers(anyInt(), anyInt());
    }

    @Test
    public void testDeleteUserById_Success() {

        when(userRepository.findUser(testUser.getUserId())).thenReturn(testUser);
        doNothing().when(userRepository).deleteUser(testUser.getUserId());

        boolean result = userService.deleteUserById(testUser.getUserId());

        assertTrue(result);
        verify(userRepository).deleteUser(testUser.getUserId());
    }

    @Test
    public void testDeleteUserById_Failure_UserNotFound() {
        int userId = 1;

        when(userRepository.findUser(userId)).thenReturn(null);

        boolean result = userService.deleteUserById(userId);

        assertFalse(result);
        verify(userRepository, never()).deleteUser(userId);
    }

    @Test
    public void testFindUserById() {

        when(userRepository.findUser(testUser.getUserId())).thenReturn(testUser);

        User result = userService.findUserById(testUser.getUserId());

        assertNotNull(result);
        verify(userRepository).findUser(testUser.getUserId());
    }

    @Test
    public void testCheckUserByMailId_UserFound() {

        when(userRepository.checkUserByEmailid(testUser.getEmailId())).thenReturn(testUser);

        User result = userRepository.checkUserByEmailid(testUser.getEmailId());

        assertNotNull(result);
        assertEquals(testUser.getEmailId(), result.getEmailId());
        verify(userRepository).checkUserByEmailid(testUser.getEmailId());
    }

    @Test
    public void testCheckUserByMailId_UserNotFound() {
        String emailId = "Harish@gmail.com";

        when(userRepository.checkUserByEmailid(emailId)).thenReturn(null);

        User result = userRepository.checkUserByEmailid(emailId);

        assertNull(result);
        verify(userRepository).checkUserByEmailid(emailId);
    }

    @Test
    public void testAuthenticateUser_Failure_InvalidPassword() {
        when(userRepository.checkUserByEmailid(testUser.getEmailId())).thenReturn(testUser);

        boolean result = userService.authenticateUser(testUser.getEmailId(), "Aravind@1", session);
        assertFalse(result);
        verify(session, times(0)).setAttribute("LoginUser", testUser);
    }

    @Test
    public void testUpdateUser_Success() {

        User updatedUser = new User("Arvind Kumar", "Aravind@1", "arvind.kumar@gmail.com",
                "1992-12-10", "Java Developer", "Admin", 1, "Male");
        when(userRepository.findUser(updatedUser.getUserId())).thenReturn(testUser);
        when(userRepository.findUserByEmailExcludingId(updatedUser.getEmailId(), testUser.getUserId()))
                .thenReturn(null);
        boolean result = userService.updateUsers(updatedUser, testUser.getUserId());
        assertTrue(result);
        verify(userRepository, times(1)).updateUser(testUser);
    }

    @Test
    public void testUpdateUser_Failure_EmailAlreadyExists() {
        User updatedUser = new User("Arvind Kumar", "Aravindh@1", "Aravindh@gmail.com",
                "1992-12-10", "Java Developer", "Admin", 1, "Male");
        when(userRepository.findUserByEmailExcludingId(updatedUser.getEmailId(), testUser.getUserId()))
                .thenReturn(testUser);

        boolean result = userService.updateUsers(updatedUser, testUser.getUserId());

        assertFalse(result);
    }

    @Test
    public void testFetchUsers() {
        when(userRepository.fetchUsersWithPagination(0, 10)).thenReturn(mock(List.class));

        List<User> users = userService.fetchInactiveUsers(1, 10);

        assertNotNull(users);
    }

    @Test
    public void testGetTotalUsers() {
        when(userRepository.countTotalUsers()).thenReturn(100);

        int totalUsers = userRepository.countTotalUsers();

        assertEquals(100, totalUsers);
        verify(userRepository, times(1)).countTotalUsers();
    }

    @Test
    public void testPrepareUserPage() {
        List<User> paginatedUsers = Arrays.asList(testUser);
        when(userRepository.fetchUsersWithPagination(0, 10)).thenReturn(paginatedUsers);
        when(userRepository.countTotalUsers()).thenReturn(50);
        userService.prepareUserPage(1, 10, session, model);

        verify(model, times(1)).addAttribute("UserList", paginatedUsers);
        verify(model, times(1)).addAttribute("currentPage", 1);
        verify(model, times(1)).addAttribute("totalPages", 5);
    }

    @Test
    public void testPrepareLoginInfoPage() {
        String userId = "1";
        User user = new User("Harish", "Harish@12", "Harish@gmail.com", "1990-01-01", "Developer", "Admin", 1, "Male");
        user.setUserId(1);

        int visitCount = 5;
        LocalDateTime loginTime = LocalDateTime.of(2024, 11, 1, 10, 0, 0, 0);
        Login login = new Login();
        login.setLogId(101);
        login.setUser(user);
        login.setLoginInfo(loginTime);

        assertEquals(101, login.getLogId());
        assertEquals(user, login.getUser());
        assertEquals(loginTime, login.getLoginInfo());
        assertEquals("10:00:00", login.getFormattedTime());
        assertEquals("01-11-2024", login.getFormattedDate());

        List<Login> logins = Arrays.asList(login);

        when(userRepository.getLoginInfo(1, 1, 10)).thenReturn(logins);
        when(userRepository.getTotalLoginCount(1)).thenReturn(15);

        userService.prepareLoginInfoPage(userId, 1, 10, model);

        verify(model, times(1)).addAttribute("userId", 1);
        verify(model, times(1)).addAttribute("Loggedinfo", logins);
        verify(model, times(1)).addAttribute("currentPage", 1);
        verify(model, times(1)).addAttribute("totalPages", 2);
        verify(model, times(1)).addAttribute("totalLogins", 15);
    }

    @Test
    public void testPrepareInactiveUsersPage() {
        int pageNumber = 1;
        int pageSize = 10;
        when(session.getAttribute("LoginUser")).thenReturn(loginUser);

        List<User> inactiveUsers = Arrays.asList(loginUser, testUser);
        when(userRepository.findInactiveUsers((pageNumber - 1) * pageSize, pageSize)).thenReturn(inactiveUsers);
        when(userRepository.countInactiveUsers()).thenReturn(25);

        userService.prepareInactiveUsersPage(pageNumber, pageSize, session, model);
        verify(model, times(1)).addAttribute("UserList", inactiveUsers);
        verify(model, times(1)).addAttribute("currentPage", pageNumber);
        verify(model, times(1)).addAttribute("totalPages", 3);
        verify(model, times(1)).addAttribute("inactiveUserCount", 25);
    }

    @Test
    public void testUpdateUserPassword_Failure() {
        when(session.getAttribute("LoginUser")).thenReturn(loginUser);
        when(userRepository.checkUserByEmailid("siva@gmail.com")).thenReturn(loginUser);

        boolean result = userService.updateUserPassword(session, "siva123", "siva@1");

        assertFalse(result);
    }

    @Test
    public void testUpdateUsers_WhenUserNotFound() {
        User updateUser = new User("Krish", "Krish@1", "krish@gmail.com", "1990-01-01", "Developer",
                "Admin", 1, "Male");
        int paramId = 1;

        when(userRepository.findUser(paramId)).thenReturn(null);

        boolean result = userService.updateUsers(updateUser, paramId);

        assertFalse(result);
        verify(userRepository, times(1)).findUser(paramId);
        verify(userRepository, never()).updateUser(any(User.class));
    }

    @Test
    public void testAuthenticateUser_WhenUserNotFoundOrPasswordDoesNotMatch() {
        String emailId = "Harish@google.com";
        String password = "Harsh@1";

        when(userRepository.checkUserByEmailid(emailId)).thenReturn(null);

        boolean result = userService.authenticateUser(emailId, password, session);

        assertFalse(result);
        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    public void testAuthenticateUser_Success() {

        String emailId = "harish@gmail.com";
        String password = "password123";
        User mockUser = new User();
        mockUser.setEmailId(emailId);
        mockUser.setPassword("$2a$12$DiSPl3FcQlKWVuFR.2MCMuA7O/bKr.kgrH35w1BB8pGeJoRnbfUVC"); 
                                                                                              

        when(userRepository.checkUserByEmailid(emailId)).thenReturn(mockUser);
        when(passwordEncoder.matches(password, mockUser.getPassword())).thenReturn(true);

        boolean result = userService.authenticateUser(emailId, password, session);

        assertTrue(result, "Authentication should succeed for valid credentials");
        verify(session).setAttribute("LoginUser", mockUser);
    }

    @Test
    public void testAuthenticateUser_Failure_UserNotFound() {
        String emailId = "nonexistent@example.com";
        String password = "password123";

        when(userRepository.checkUserByEmailid(emailId)).thenReturn(null);

        boolean result = userService.authenticateUser(emailId, password, session);

        assertFalse(result, "Authentication should fail when user is not found");
        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    public void testUpdateUserPassword_Failure_OldPasswordMismatch() {
        String oldPassword = "incorrectOldPassword";
        String newPassword = "newPassword123";
        String emailId = "Harish@gmail.com";

        User mockUser = new User();
        mockUser.setEmailId(emailId);
        mockUser.setPassword(passwordEncoder.encode("correctOldPassword"));

        HttpSession mockSession = mock(HttpSession.class);
        when(mockSession.getAttribute("LoginUser")).thenReturn(mockUser);

        when(userRepository.checkUserByEmailid(emailId)).thenReturn(mockUser);

        when(passwordEncoder.matches(oldPassword, mockUser.getPassword())).thenReturn(false);
        boolean result = userService.updateUserPassword(mockSession, oldPassword, newPassword);

        assertFalse(result, "Password update  failed as old password is incorrect");
        verify(userRepository, never()).updateUser(any(User.class));
    }

    @Test
    public void testUpdateUserPassword_Success() {
        String oldPassword = "password123";
        String newPassword = "newPassword123";

        User mockUser = new User();
        mockUser.setEmailId("Harish@gmail.com");
        mockUser.setPassword("$2a$12$jQvyjTYCRGAjZnihnbbR9uIPb9jUvmOhUdhFcOI1dm0Xo88q7Ykqe");

        HttpSession mockSession = mock(HttpSession.class);
        when(mockSession.getAttribute("LoginUser")).thenReturn(mockUser);

        when(passwordEncoder.matches(oldPassword, mockUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword))
                .thenReturn("$2a$12$w1HRp64Q1UrkI4i52j8VOOcDMg.APHUcVo6IaqF44J0BmAUAM.3Pi");

        when(userRepository.checkUserByEmailid(mockUser.getEmailId())).thenReturn(mockUser);

        boolean result = userService.updateUserPassword(mockSession, oldPassword, newPassword);
        assertTrue(result);
        verify(userRepository).updateUser(mockUser);
    }


}
