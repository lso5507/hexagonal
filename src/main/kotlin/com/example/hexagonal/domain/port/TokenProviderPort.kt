package com.example.hexagonal.domain.port

import com.example.hexagonal.domain.port.dto.TokenResponse

interface TokenProviderPort {
    fun generateToken(email: String, roles: List<String>): TokenResponse
}
