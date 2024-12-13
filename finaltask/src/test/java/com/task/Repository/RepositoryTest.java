package com.task.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.task.Model.Login;
import com.task.Model.User;

public class RepositoryTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Criteria criteria;
    private User testUser;
    private Login login;
    private User loginUser;

    @InjectMocks
    private UserRepository userRepository;
    private int offset = 0;
    private int pageSize = 5;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User("Arvind Kumar", "Aravind@1", "arvind.kumar@gmail.com",
                "1992-12-10", "Software Engineer", "Admin", 1, "Male");

        loginUser = new User("Siva", "siva123", "siva@gmail.com", "1990-01-01", "Developer", "Admin", 1,
                "Male");

        LocalDateTime loginTime = LocalDateTime.now();

        login = new Login(testUser, 5, loginTime);
        login.setUser(testUser);
        login.getUser();
        testUser.getLoginStatus();
        LocalDateTime loginTime1 = LocalDateTime.now().minusDays(1);
        LocalDateTime loginTime2 = LocalDateTime.now();

        Set<Login> logins = new HashSet<>();
        logins.add(new Login(testUser, 1, loginTime1));
        logins.add(new Login(testUser, 2, loginTime2));
        testUser.setLogins(logins);
        testUser.getLogins();

        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(session.createCriteria(User.class)).thenReturn(criteria);
        when(criteria.add(any())).thenReturn(criteria);
        when(criteria.setFirstResult(anyInt())).thenReturn(criteria);
        when(criteria.setMaxResults(anyInt())).thenReturn(criteria);
        when(criteria.setFirstResult(offset)).thenReturn(criteria);
        when(criteria.setMaxResults(pageSize)).thenReturn(criteria);
        when(criteria.setProjection(Projections.rowCount())).thenReturn(criteria);
        when(session.createCriteria(Login.class)).thenReturn(criteria);
        when(criteria.createAlias("user", "u")).thenReturn(criteria);

    }

    @Test
    public void testSaveLoginInfo_Success() {
        userRepository.saveLoginInfo(login);

        verify(sessionFactory, times(1)).getCurrentSession();
        verify(session, times(1)).save(login);
    }

    @Test
    public void testSaveLoginInfo_HibernateException() {
        doThrow(new HibernateException("Test Exception")).when(session).save(login);

        userRepository.saveLoginInfo(login);

        verify(sessionFactory, times(1)).getCurrentSession();
    }

    @Test
    public void testSaveLoginInfo_GenericException() {
        doThrow(new RuntimeException("Test Exception")).when(session).save(login);

        userRepository.saveLoginInfo(login);

        verify(sessionFactory, times(1)).getCurrentSession();
    }

    @Test
    public void testAddUserInfo_Success() {

        when(session.save(testUser)).thenReturn(1);
        boolean result = userRepository.addUserInfo(testUser);
        verify(session, times(1)).save(testUser);
        assertTrue(result);
    }

    @Test
    public void testAddUserInfo_runtimeexception() {

        doThrow(new RuntimeException("Exception")).when(session).save(testUser);
        boolean result = userRepository.addUserInfo(testUser);
        verify(session, times(1)).save(testUser);
        assertFalse(result);
    }

    @Test
    public void testAddUserInfo_HibernateException() {

        doThrow(new HibernateException("Exception")).when(session).save(testUser);
        boolean result = userRepository.addUserInfo(testUser);
        verify(session, times(1)).save(testUser);
        assertFalse(result);
    }

    @Test
    public void checkUserByEmailid_shouldReturnUser_whenUserExists() {

        when(criteria.uniqueResult()).thenReturn(testUser);
        User result = userRepository.checkUserByEmailid(testUser.getEmailId());
        assertNotNull(result);
        assertEquals(testUser.getEmailId(), result.getEmailId());
        verify(criteria, times(1)).add(any());
        verify(criteria, times(1)).uniqueResult();
    }

    @Test
    public void checkUserByEmailid_shouldReturnNull_whenUserDoesNotExist() {
        String emailId = "Harish@gmail.com";
        when(criteria.uniqueResult()).thenReturn(null);
        User result = userRepository.checkUserByEmailid(emailId);
        assertNull(result);

    }

    @Test
    public void checkUserByEmailid_shouldReturnNull_whenHibernateException() {
        String emailId = "Harish@gmail.com";

        when(criteria.add(any())).thenThrow(new HibernateException("Test Exception"));
        User result = userRepository.checkUserByEmailid(emailId);
        assertNull(result);
    }

    @Test
    public void checkUserByEmailid_shouldReturnNull_whenGenericException() {
        String emailId = "Harish@gmail.com";
        when(criteria.add(any())).thenThrow(new RuntimeException());
        User result = userRepository.checkUserByEmailid(emailId);
        assertNull(result);
    }

    @Test
    public void testDeleteUser() {
        int userId = 1;
        User user = new User();
        user.setUserId(userId);
        user.setRole("admin");
        user.getRole();

        when(session.get(User.class, userId)).thenReturn(user);
        doNothing().when(session).delete(user);

        userRepository.deleteUser(userId);
        verify(session, times(1)).get(User.class, userId);
        verify(session, times(1)).delete(user);
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        int userId = 1;

        when(session.get(User.class, userId)).thenReturn(null);
        userRepository.deleteUser(userId);
        verify(session, times(1)).get(User.class, userId);
        verify(session, times(0)).delete(any());
    }

    @Test
    public void testDeleteUser_HibernateException() {
        int userId = 1;
        when(session.get(User.class, userId)).thenThrow(new HibernateException("Mock HibernateException"));

        assertDoesNotThrow(() -> userRepository.deleteUser(userId));

    }

    @Test
    public void testDeleteUser_GeneralException() {
        int userId = 1;
        when(session.get(User.class, userId)).thenThrow(new RuntimeException("Mock General Exception"));

        assertDoesNotThrow(() -> userRepository.deleteUser(userId));

    }

    @Test
    public void countInactiveUsers() {
        when(session.createCriteria(User.class)).thenReturn(criteria);

        when(criteria.add(Restrictions.eq("loginStatus", 0))).thenReturn(criteria);
        when(criteria.uniqueResult()).thenReturn(5L);
        int result = userRepository.countInactiveUsers();

        assertEquals(5, result);
    }

    @Test
    public void countInactiveUsers_whenNoInactiveUsers() {
        when(criteria.uniqueResult()).thenReturn(null);
        int result = userRepository.countInactiveUsers();
        assertEquals(0, result);
    }

    @Test
    public void countInactiveUsers_whenHibernateExceptionOccurs() {
        when(sessionFactory.getCurrentSession()).thenThrow(new HibernateException("Simulated Hibernate exception"));
        int result = userRepository.countInactiveUsers();
        assertEquals(0, result);
    }

    @Test
    public void countInactiveUsers_whenGenericExceptionOccurs() {
        when(session.createCriteria(User.class)).thenThrow(new RuntimeException("Test Generic Exception"));

        int result = userRepository.countInactiveUsers();
        assertEquals(0, result);
    }

    @Test
    public void findInactiveUsers_shouldReturnInactiveUsers_whenUsersExist() {

        List<User> expectedUsers = new ArrayList<>(Arrays.asList(testUser, loginUser));
        when(criteria.list()).thenReturn(expectedUsers);
        List<User> result = userRepository.findInactiveUsers(offset, pageSize);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedUsers, result);
    }

    @Test
    public void findInactiveUsers_shouldReturnEmptyList_whenHibernateExceptionOccurs() {
        when(sessionFactory.getCurrentSession()).thenThrow(new HibernateException("Simulated Hibernate exception"));
        List<User> result = userRepository.findInactiveUsers(offset, pageSize);
        assertNull(result);
    }

    @Test
    public void findInactiveUsers_shouldReturnNull_whenGenericExceptionOccurs() {

        when(criteria.list()).thenThrow(new RuntimeException("Test Generic Exception"));
        List<User> result = userRepository.findInactiveUsers(offset, pageSize);
        assertNull(result);
    }

    @Test
    public void getTotalLoginCount_shouldReturnLoginCount_whenExists() {
        int userId = 12;
        long expectedCount = 5L;

        when(session.createCriteria(Login.class)).thenReturn(criteria);
        when(criteria.uniqueResult()).thenReturn(expectedCount);
        int result = userRepository.getTotalLoginCount(userId);
        assertEquals(5, result);
    }

    @Test
    public void getTotalLoginCount_shouldReturnZero_whenNoLogins() {
        int userId = 5;
        Long expectedCount = null;
        when(criteria.uniqueResult()).thenReturn(expectedCount);
        int result = userRepository.getTotalLoginCount(userId);

        assertEquals(0, result);
    }

    @Test
    public void getTotalLoginCount_shouldHandleHibernateException() {
        int userId = 5;
        when(criteria.uniqueResult()).thenThrow(new HibernateException("Test Exception"));
        int result = userRepository.getTotalLoginCount(userId);
        assertEquals(0, result);
    }

    @Test
    public void countTotalUsers_shouldReturnTotalCount_whenUsersExist() {
        long expectedCount = 3;
        when(criteria.uniqueResult()).thenReturn(expectedCount);
        int result = userRepository.countTotalUsers();
        assertEquals(3, result);
    }

    @Test
    public void countTotalUsers_shouldHandleHibernateException() {

        when(criteria.uniqueResult()).thenThrow(new HibernateException("Test Exception"));
        int result = userRepository.countTotalUsers();
        assertEquals(0, result);
    }

    @Test
    public void countTotalUsers_shouldHandleException() {

        when(criteria.uniqueResult()).thenThrow(new RuntimeException("Test Generic Exception"));
        int result = userRepository.countTotalUsers();
        assertEquals(0, result);
    }

    @Test
    public void findUser_shouldReturnUser_whenUserExists() {
        int userId = 1;
        User mockUser = new User();
        mockUser.setUserId(userId);

        when(session.get(User.class, userId)).thenReturn(mockUser);
        User result = userRepository.findUser(userId);
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
    }

    @Test
    public void findUser_shouldReturnNull_whenUserDoesNotExist() {
        int userId = 999;

        when(session.get(User.class, userId)).thenReturn(null);
        User result = userRepository.findUser(userId);
        assertNull(result);
    }

    @Test
    public void findUser_shouldHandleHibernateException() {
        int userId = 1;

        when(session.get(User.class, userId)).thenThrow(new HibernateException("Database error"));
        User result = userRepository.findUser(userId);
        assertNull(result);
    }

    @Test
    public void findUser_shouldHandleGenericException() {
        int userId = 1;

        when(session.get(User.class, userId)).thenThrow(new RuntimeException());
        User result = userRepository.findUser(userId);
        assertNull(result);

    }

    @Test
    public void getLoginInfo_shouldReturnList_whenValidUserId() {
        int userId = 1;
        int page = 1;

        List<Login> mockLoginList = Arrays.asList(new Login(), new Login());
        when(criteria.setFirstResult((page - 1) * pageSize)).thenReturn(criteria);
        when(criteria.list()).thenReturn(mockLoginList);

        List<Login> result = userRepository.getLoginInfo(userId, page, pageSize);

        assertNotNull(result);
        assertEquals(mockLoginList.size(), result.size());
    }

    @Test
    public void getLoginInfo_shouldReturnEmptyList_whenNoLogins() {
        int userId = 1;
        int page = 1;
        when(criteria.setMaxResults(pageSize)).thenReturn(criteria);
        when(criteria.list()).thenReturn(Collections.emptyList());

        List<Login> result = userRepository.getLoginInfo(userId, page, pageSize);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void getLoginInfo_shouldHandleHibernateException() {
        int userId = 1;
        int page = 1;

        when(criteria.setFirstResult((page - 1) * pageSize)).thenReturn(criteria);
        when(criteria.setMaxResults(pageSize)).thenReturn(criteria);
        when(criteria.list()).thenThrow(new HibernateException("Database error"));

        List<Login> result = userRepository.getLoginInfo(userId, page, pageSize);

        assertNull(result);

    }

    @Test
    public void getLoginInfo_shouldHandleGeneralException() {
        int userId = 1;
        int page = 1;
        when(criteria.setFirstResult((page - 1) * pageSize)).thenReturn(criteria);
        when(criteria.setMaxResults(pageSize)).thenReturn(criteria);
        when(criteria.list()).thenThrow(new RuntimeException());

        List<Login> result = userRepository.getLoginInfo(userId, page, pageSize);
        assertNull(result);

    }

    @Test
    public void getLoginInfo_shouldReturnResultsWithPagination() {
        int userId = 1;
        int page = 2;
        List<Login> mockLoginList = Arrays.asList(new Login(), new Login());
        when(criteria.setFirstResult((page - 1) * pageSize)).thenReturn(criteria);
        when(criteria.setMaxResults(pageSize)).thenReturn(criteria);
        when(criteria.list()).thenReturn(mockLoginList);

        List<Login> result = userRepository.getLoginInfo(userId, page, pageSize);
        assertNotNull(result);
        assertEquals(mockLoginList.size(), result.size());
    }

    @Test
    public void findUserByEmailExcludingId_shouldReturnUser_whenUserExists() {
        String emailId = "Hari@gmail.com";
        int userId = 1;
        User user = new User();
        user.setEmailId(emailId);
        user.setUserId(2);

        when(criteria.add(Restrictions.eq("emailId", emailId))).thenReturn(criteria);
        when(criteria.add(Restrictions.ne("userId", userId))).thenReturn(criteria);
        when(criteria.uniqueResult()).thenReturn(user);

        User result = userRepository.findUserByEmailExcludingId(emailId, userId);
        assertNotNull(result);
        assertEquals(user, result);

    }

    @Test
    public void findUserByEmailExcludingId_shouldReturnNull_whenUserNotFound() {
        String emailId = "rohan@gmail.com";
        int userId = 1;

        when(criteria.uniqueResult()).thenReturn(null);
        User result = userRepository.findUserByEmailExcludingId(emailId, userId);
        assertNull(result);
    }

    @Test
    public void findUserByEmailExcludingId_shouldHandleHibernateException() {
        String emailId = "Ravi@.com";
        int userId = 5;
        when(criteria.uniqueResult()).thenThrow(new HibernateException("Database error"));
        User result = userRepository.findUserByEmailExcludingId(emailId, userId);
        assertNull(result);

    }

    @Test
    public void findUserByEmailExcludingId_shouldHandleGeneralException() {
        String emailId = "ravi@gamil.com";
        int userId = 12;
        when(criteria.uniqueResult()).thenThrow(new RuntimeException());
        User result = userRepository.findUserByEmailExcludingId(emailId, userId);
        assertNull(result);

    }

    @Test
    public void updateUser_shouldUpdateUserSuccessfully() {
        User user = new User();
        user.setUserId(1);

        userRepository.updateUser(user);
        verify(session).update(user);
    }

    @Test
    public void updateUser_shouldHandleHibernateException() {

        doThrow(new HibernateException("Database error")).when(session).update(loginUser);

        userRepository.updateUser(loginUser);

        verify(session).update(loginUser);
    }

    @Test
    public void updateUser_shouldHandleException() {

        doThrow(new RuntimeException()).when(session).update(testUser);

        userRepository.updateUser(testUser);
        verify(session).update(testUser);
    }

    @Test
    public void fetchUsersWithPagination_shouldReturnList_whenUsersExist() {
        List<User> mockUserList = Arrays.asList(new User(), new User());
        when(criteria.list()).thenReturn(mockUserList);
        List<User> result = userRepository.fetchUsersWithPagination(offset, pageSize);
        assertNotNull(result);
        assertEquals(mockUserList.size(), result.size());
    }

    @Test
    public void fetchUsersWithPagination_shouldReturnEmptyList_whenNoUsersFound() {
        List<User> result = userRepository.fetchUsersWithPagination(offset, pageSize);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void fetchUsersWithPagination_shouldHandleHibernateException() {

        when(criteria.list()).thenThrow(new HibernateException("Database error"));
        List<User> result = userRepository.fetchUsersWithPagination(offset, pageSize);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void countInactiveUsers_shouldReturnZero_whenNoInactiveUsers() {
        Long expectedCount = 0L;

        when(criteria.add(Restrictions.eq("loginStatus", 0))).thenReturn(criteria);
        when(criteria.uniqueResult()).thenReturn(expectedCount);
        int result = userRepository.countInactiveUsers();
        assertEquals(0, result);
    }

    @Test
    public void testSearchResults_Success() {

        List<User> mockUsers = Arrays.asList(testUser);

        when(criteria.add(any())).thenReturn(criteria);
        when(criteria.list()).thenReturn(mockUsers);

        List<User> result = userRepository.searchResults("vin");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Arvind Kumar", result.get(0).getUserName());
    }

    @Test
    public void testSearchResults_HibernateException() {
        when(sessionFactory.getCurrentSession()).thenThrow(new HibernateException("Hibernate error"));

        List<User> result = userRepository.searchResults("vin");

        assertNull(result);
    }

    @Test
    public void testSearchResults_GenericException() {
        when(sessionFactory.getCurrentSession()).thenThrow(new RuntimeException("Generic error"));

        List<User> result = userRepository.searchResults("vin");

        assertNull(result);
    }

}
