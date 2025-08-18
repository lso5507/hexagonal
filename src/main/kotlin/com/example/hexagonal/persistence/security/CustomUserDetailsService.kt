package com.example.hexagonal.persistence.security

import com.example.hexagonal.domain.port.UserOutPort
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userOutPort: UserOutPort
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userOutPort.findByEmail(username)
            ?: throw UsernameNotFoundException("User not found with email: ${"$"}username")

        return User.builder()
            .username(user.email)
            .password(user.password)
            .roles(*user.roles.toTypedArray())
            .build()
    }
}
