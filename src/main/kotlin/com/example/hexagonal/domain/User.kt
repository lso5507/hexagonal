package com.example.hexagonal.domain

import java.time.LocalDateTime // Added import

data class User(
    val id: Long? = null, // Added id
    val name: String,
    val email: String,
    var password: String, // Changed to var
    val roles: MutableList<String> = mutableListOf("USER"), // Added roles
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)