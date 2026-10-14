package com.trading.app.security.config;

import com.trading.app.login.model.SignupRequest;
import com.trading.app.login.model.SignupResponse;

public interface AuthService {
    String signup(SignupRequest request);

    SignupResponse login(SignupRequest request);
}
