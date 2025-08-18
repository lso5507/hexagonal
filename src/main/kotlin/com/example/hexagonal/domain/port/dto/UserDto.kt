package com.example.hexagonal.domain.port.dto

import com.example.hexagonal.domain.User
import java.time.LocalDateTime

data class UserCreateRequest(
    val name: String,
    val email: String,
    val password: String
) {
    fun toDomain(): User {
        return User(
            name = this.name,
            email = this.email,
            password = this.password
        )
    }
}

data class UserUpdateRequest(
    val id: Long, // Added id
    val name: String,
    val email: String,
    val password: String? = null
) {
    fun toDomain(): User { // Removed id parameter as it's now part of the DTO
        return User(
            id = this.id,
            name = this.name,
            email = this.email,
            password = this.password ?: ""
        )
    }
}

data class UserResponse(
    val id: Long, // Added id
    val name: String,
    val email: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromDomain(user: User): UserResponse {
            return UserResponse(
                id = user.id!!, // Added id
                name = user.name,
                email = user.email,
                createdAt = user.createdAt!!,
                updatedAt = user.updatedAt!!
            )
        }
    }
}