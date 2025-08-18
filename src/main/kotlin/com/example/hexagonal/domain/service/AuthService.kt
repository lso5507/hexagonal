package com.example.hexagonal.domain.service

import com.example.hexagonal.domain.exception.InvalidCredentialsException
import com.example.hexagonal.domain.port.AuthInPort
import com.example.hexagonal.domain.port.PasswordEncoderPort
import com.example.hexagonal.domain.port.TokenProviderPort
import com.example.hexagonal.domain.port.UserOutPort
import com.example.hexagonal.domain.port.dto.LoginRequest
import com.example.hexagonal.domain.port.dto.TokenResponse
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userOutPort: UserOutPort,
    private val passwordEncoderPort: PasswordEncoderPort,
    private val tokenProviderPort: TokenProviderPort
) : AuthInPort {

    override fun login(loginRequest: LoginRequest): TokenResponse {
        val user = userOutPort.findByEmail(loginRequest.email)
            ?: throw InvalidCredentialsException("Invalid email or password")

        if (!passwordEncoderPort.matches(loginRequest.password, user.password)) {
            throw InvalidCredentialsException("Invalid email or password")
        }

        return tokenProviderPort.generateToken(user.email, user.roles)
    }
}
