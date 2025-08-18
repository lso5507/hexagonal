package com.example.hexagonal.domain.port.dto

data class LoginRequest(
    val email: String,
    val password: String
)
