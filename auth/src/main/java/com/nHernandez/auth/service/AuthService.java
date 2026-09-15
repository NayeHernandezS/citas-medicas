package com.nHernandez.auth.service;

import com.nHernandez.auth.dto.LoginRequest;
import com.nHernandez.auth.dto.TokenResponse;

public interface AuthService {
    TokenResponse autenticar(LoginRequest request) throws Exception;

}
