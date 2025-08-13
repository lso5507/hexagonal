package com.example.hexagonal.domain

import java.time.LocalDateTime // Added import

data class User(
    val id: Long? = null, // Added id
    val name: String,
    val email: String,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)