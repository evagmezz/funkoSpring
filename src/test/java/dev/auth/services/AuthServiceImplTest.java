package dev.auth.services;

import dev.rest.auth.dto.JwtAuthResponse;
import dev.rest.auth.dto.UserSignInRequest;
import dev.rest.auth.dto.UserSignUpRequest;
import dev.rest.auth.exceptions.AuthSingInInvalid;
import dev.rest.auth.exceptions.UserAuthNameOrEmailExist;
import dev.rest.auth.exceptions.UserDiferentPasswords;
import dev.rest.auth.repositories.AuthUsersRepository;
import dev.rest.auth.services.authentication.AuthServiceImpl;
import dev.rest.auth.services.jwt.JwtService;
import dev.rest.users.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private AuthUsersRepository authUsersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void signUp() {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("user");
        request.setPassword("password");
        request.setPasswordComprobacion("password");
        request.setEmail("user@gmail.com");
        request.setNombre("User");
        request.setApellidos("User");

        User userStored = new User();
        when(authUsersRepository.save(any(User.class))).thenReturn(userStored);

        String token = "token";
        when(jwtService.generateToken(userStored)).thenReturn(token);

        JwtAuthResponse response = authService.signUp(request);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(token, response.getToken()),
                () -> verify(authUsersRepository, times(1)).save(any(User.class)),
                () -> verify(jwtService, times(1)).generateToken(userStored)
        );
    }

    @Test
    void signUpUserDiferentPasswords() {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("testuser");
        request.setPassword("password1");
        request.setPasswordComprobacion("password2");
        request.setEmail("test@example.com");
        request.setNombre("Test");
        request.setApellidos("User");

        assertThrows(UserDiferentPasswords.class, () -> authService.signUp(request));
    }

    @Test
    void signUpUserExists() {
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("user");
        request.setPassword("password");
        request.setPasswordComprobacion("password");
        request.setEmail("user@gmail.com");
        request.setNombre("User");
        request.setApellidos("User");

        when(authUsersRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(UserAuthNameOrEmailExist.class, () -> authService.signUp(request));

        verify(authUsersRepository, times(1)).save(any(User.class));
    }

    @Test
    public void signIn() {
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("user");
        request.setPassword("password");

        User user = new User();
        when(authUsersRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));

        String token = "token";
        when(jwtService.generateToken(user)).thenReturn(token);

        JwtAuthResponse response = authService.signIn(request);

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(token, response.getToken()),
                () -> verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class)),
                () -> verify(authUsersRepository, times(1)).findByUsername(request.getUsername()),
                () -> verify(jwtService, times(1)).generateToken(user)
        );
    }

    @Test
    public void signInInvalid() {
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("user");
        request.setPassword("password");

        when(authUsersRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        assertThrows(AuthSingInInvalid.class, () -> authService.signIn(request));
    }
}
