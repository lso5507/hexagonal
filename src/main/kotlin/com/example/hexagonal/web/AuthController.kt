package com.example.hexagonal.web

import com.example.hexagonal.domain.port.AuthInPort
import com.example.hexagonal.domain.port.dto.LoginRequest
import com.example.hexagonal.domain.port.dto.TokenResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authInPort: AuthInPort
) {

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<TokenResponse> {
        val tokenResponse = authInPort.login(loginRequest)
        return ResponseEntity.ok(tokenResponse)
    }
}
