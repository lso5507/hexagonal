package com.example.hexagonal.domain.port

interface PasswordEncoderPort {
    fun encode(password: String): String
    fun matches(raw: String, encoded: String): Boolean
}
