package com.example.hexagonal.infrastructure.security

import com.example.hexagonal.domain.port.PasswordEncoderPort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncoderAdapter(
    private val passwordEncoder: PasswordEncoder
) : PasswordEncoderPort {

    override fun encode(password: String): String {
        return passwordEncoder.encode(password)
    }

    override fun matches(raw: String, encoded: String): Boolean {
        return passwordEncoder.matches(raw, encoded)
    }
}
