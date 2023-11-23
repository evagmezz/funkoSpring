package dev.auth.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.rest.auth.dto.JwtAuthResponse;
import dev.rest.auth.dto.UserSignInRequest;
import dev.rest.auth.dto.UserSignUpRequest;
import dev.rest.auth.exceptions.AuthSingInInvalid;
import dev.rest.auth.exceptions.UserAuthNameOrEmailExist;
import dev.rest.auth.exceptions.UserDiferentPasswords;
import dev.rest.auth.services.authentication.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    private final String myEndpoint = "/api/auth";
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    @MockBean
    private AuthService authService;


    @Autowired
    public AuthControllerTest(AuthService authService) {
        this.authService = authService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void signUp() throws Exception {
        var userSignUpRequest = new UserSignUpRequest("User", "User", "user", "user@gmail.com", "user1234", "user1234");
        var jwtAuthResponse = new JwtAuthResponse("token");
        var myLocalEndpoint = myEndpoint + "/signup";
        when(authService.signUp(any(UserSignUpRequest.class))).thenReturn(jwtAuthResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userSignUpRequest)))
                .andReturn().getResponse();

        JwtAuthResponse res = mapper.readValue(response.getContentAsString(), JwtAuthResponse.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("token", res.getToken())
        );

        verify(authService, times(1)).signUp(any(UserSignUpRequest.class));
    }

    @Test
    void signUpUserDiferentPassword() {
        var myLocalEndpoint = myEndpoint + "/signup";
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("user");
        request.setPassword("password");
        request.setPasswordComprobacion("password2");
        request.setEmail("user@gmail.com");
        request.setNombre("User");
        request.setApellidos("User");

        when(authService.signUp(any(UserSignUpRequest.class))).thenThrow(new UserDiferentPasswords("Las contraseñas no coinciden"));

        assertThrows(UserDiferentPasswords.class, () -> authService.signUp(request));

        verify(authService, times(1)).signUp(any(UserSignUpRequest.class));
    }

    @Test
    void signUpUserExist() {
        var myLocalEndpoint = myEndpoint + "/signup";
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("user");
        request.setPassword("password");
        request.setPasswordComprobacion("password");
        request.setEmail("user@gmail.com");
        request.setNombre("User");
        request.setApellidos("User");

        when(authService.signUp(any(UserSignUpRequest.class))).thenThrow(new UserAuthNameOrEmailExist("El usuario con username " + request.getUsername() + " o email " + request.getEmail() + " ya existe"));

        assertThrows(UserAuthNameOrEmailExist.class, () -> authService.signUp(request));

        verify(authService, times(1)).signUp(any(UserSignUpRequest.class));
    }

    @Test
    void signUpBadRequest() throws Exception {
        var myLocalEndpoint = myEndpoint + "/signup";
        UserSignUpRequest request = new UserSignUpRequest();
        request.setUsername("");
        request.setPassword("password");
        request.setPasswordComprobacion("password");
        request.setEmail("");
        request.setNombre("");
        request.setApellidos("");

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse();

        System.out.println(response.getContentAsString());

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("El nombre de usuario no puede"))
        );
    }

    @Test
    void signIn() throws Exception {
        var userSignUpRequest = new UserSignUpRequest("User", "User", "user", "user@gmail.com", "user1234", "user1234");
        var jwtAuthResponse = new JwtAuthResponse("token");
        var myLocalEndpoint = myEndpoint + "/signin";
        when(authService.signIn(any(UserSignInRequest.class))).thenReturn(jwtAuthResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userSignUpRequest)))
                .andReturn().getResponse();

        JwtAuthResponse res = mapper.readValue(response.getContentAsString(), JwtAuthResponse.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("token", res.getToken())
        );

        verify(authService, times(1)).signIn(any(UserSignInRequest.class));
    }


    @Test
    void signInInvalid() {
        var myLocalEndpoint = myEndpoint + "/signin";
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("user");
        request.setPassword("<PASSWORD>");

        when(authService.signIn(any(UserSignInRequest.class))).thenThrow(new AuthSingInInvalid("Usuario o contraseña incorrectos"));

        assertThrows(AuthSingInInvalid.class, () -> authService.signIn(request));

        verify(authService, times(1)).signIn(any(UserSignInRequest.class));
    }

    @Test
    void signInBadRequest() throws Exception {
        var myLocalEndpoint = myEndpoint + "/signin";
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername("");
        request.setPassword("");

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andReturn().getResponse();

        System.out.println(response.getContentAsString());

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("El nombre de usuario no puede"))

        );
    }

}


