package com.example.hexagonal.domain.port

import com.example.hexagonal.domain.port.dto.LoginRequest
import com.example.hexagonal.domain.port.dto.TokenResponse

interface AuthInPort {
    fun login(loginRequest: LoginRequest): TokenResponse
}
