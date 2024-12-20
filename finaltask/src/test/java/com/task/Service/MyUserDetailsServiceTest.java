package com.task.Service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.task.Model.User;
import com.task.Model.UserPrincipal;
import com.task.Repository.UserRepository;

public class MyUserDetailsServiceTest {

    @Mock
    private UserRepository repo;

    @InjectMocks
    private MyUserDetailsService myUserDetailsService;

    private User user;
    String email;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        user = new User("Arvind Kumar", "Aravind@1", "arvind.kumar@gmail.com", "1992-12-10", "Software Engineer", "Admin", 1, "Male");
        email = "arvind.kumar@gmail.com";
    }

    @Test
    public void testLoadUserByUsername_UserExists() {
        when(repo.checkUserByEmailid(email)).thenReturn(user);

        UserPrincipal userPrincipal = (UserPrincipal) myUserDetailsService.loadUserByUsername(email);
        userPrincipal.getAuthorities();
        userPrincipal.isAccountNonExpired();
        userPrincipal.isAccountNonLocked();
        userPrincipal.isCredentialsNonExpired();
        userPrincipal.isEnabled();
        userPrincipal.getPassword();
        userPrincipal.getUser();
        assertNotNull(userPrincipal);
        assertEquals(user.getEmailId(), userPrincipal.getUsername());
        verify(repo, times(1)).checkUserByEmailid(email);
    }

    @Test
    public void testLoadUserByUsername_UserDoesNotExist() {

        when(repo.checkUserByEmailid(email)).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            myUserDetailsService.loadUserByUsername(email);
        });
    }

    @Test
    public void testLoadUserByUsername_UserNull() {

        when(repo.checkUserByEmailid(email)).thenReturn(null);
        assertThrows(UsernameNotFoundException.class, () -> {
            myUserDetailsService.loadUserByUsername(email);
        });
    }
}
