package com.motorepuestos.inventario.auth.service;

import com.motorepuestos.inventario.auth.dto.LoginRequest;
import com.motorepuestos.inventario.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}