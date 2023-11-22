package dev.rest.auth.services.authentication;

import dev.rest.auth.dto.JwtAuthResponse;
import dev.rest.auth.dto.UserSignInRequest;
import dev.rest.auth.dto.UserSignUpRequest;

public interface AuthService {

    JwtAuthResponse signUp(UserSignUpRequest request);

    JwtAuthResponse signIn(UserSignInRequest request);
}
