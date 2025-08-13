package com.example.hexagonal.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime // Added import

class UserTest {
    @Test
    fun userCreation() {
        val user = User(id = 1L, name = "test", email = "test@example.com", createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        assertNotNull(user.id)
        assertEquals("test", user.name)
        assertEquals("test@example.com", user.email)
    }

    @Test
    fun userUpdate() {
        val user = User(id = 1L, name = "test", email = "test@example.com", createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        val updatedUser = user.copy(name = "updatedTest", email = "updated@example.com")
        assertEquals("updatedTest", updatedUser.name)
        assertEquals("updated@example.com", updatedUser.email)
    }
}