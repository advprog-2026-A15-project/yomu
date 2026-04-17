package id.ac.ui.cs.advprog.yomu.authentication.config;

import id.ac.ui.cs.advprog.yomu.authentication.service.CustomUserDetailsService;
import id.ac.ui.cs.advprog.yomu.authentication.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private final String token = "token";
    private final String username = "user";
    @Mock
    private JwtService jwtService;
    @Mock
    private CustomUserDetailsService userDetailsService;
    @Mock
    private FilterChain filterChain;
    @Mock
    private HttpServletResponse response;
    @InjectMocks
    private JwtAuthenticationFilter filter;
    private MockHttpServletRequest request;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_NoHeader() throws ServletException, IOException {
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void doFilterInternal_HeaderNotBearer() throws ServletException, IOException {
        request.addHeader("Authorization", "Basic test");
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void doFilterInternal_UsernameNull() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtService).extractUsername(token);
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void doFilterInternal_TokenInvalid() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer " + token);

        UserDetails user = new User(username, "pass", Collections.emptyList());

        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(user);
        when(jwtService.validateToken(token, user)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtService).extractUsername(token);
        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtService).validateToken(token, user);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_TokenValid() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer " + token);

        UserDetails user = new User(username, "pass", Collections.emptyList());

        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(user);
        when(jwtService.validateToken(token, user)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtService).extractUsername(token);
        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtService).validateToken(token, user);
        verify(filterChain).doFilter(request, response);
    }
}