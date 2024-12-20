package com.task.Service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import com.task.Security.JwtService;

public class JwtServiceTest {

    @Mock
    private JwtService jwtService;

    private String emailId = "user@example.com";
    private String token;

    @Mock
    private UserDetails userDetails;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(userDetails.getUsername()).thenReturn(emailId);

        jwtService = new JwtService();
        token = jwtService.generateToken(emailId);
    }

    @Test
    public void testGenerateToken() {
        String generatedToken = jwtService.generateToken(emailId);
        assertNotNull(generatedToken);
        assertTrue(generatedToken.length() > 0);
    }

    @Test
    public void testExtractUsername() {
        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(emailId, extractedUsername);
    }

    @Test
    public void testIsTokenExpired() {
        boolean isExpired = jwtService.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    public void testValidateToken_validToken() {

        boolean isValid = jwtService.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    public void testValidateToken_invalidToken() {

        boolean isValid = jwtService.validateToken("invalidToken", userDetails);

        assertFalse(isValid);
    }
    @Test
    public void testValidateToken_tokenExpired() {
        JwtService jwtService = Mockito.mock(JwtService.class);

        when(jwtService.isTokenExpired(token)).thenReturn(true);

        boolean isValid = jwtService.validateToken(token, userDetails);

        assertFalse(isValid);
    }

    @Test
    public void testValidateToken_tokenExpiredInvalid() {
        JwtService jwtService = Mockito.mock(JwtService.class);

        when(jwtService.isTokenExpired(token)).thenReturn(false);
        UserDetails userDetails = Mockito.mock(UserDetails.class);
        jwtService = new JwtService();

        when(userDetails.getUsername()).thenReturn("user@example.com");


        boolean isValid = jwtService.validateToken(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    public void testValidateToken_usernameMismatch() {
        String wrongToken = jwtService.generateToken("wronguser@example.com");

        boolean isValid = jwtService.validateToken(wrongToken, userDetails);

        assertFalse(isValid);
    }
}
