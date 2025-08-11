package com.example.hexagonal.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UserTest {
    @Test
    fun validatePassword() {
        val user = User(1L,name="test",age=10)
        val userPassword = "TEST"
        val orgPassword = "TEST"
        assertTrue(user.validatePassword(userPassword,orgPassword))
    }
    @Test
    fun validatePasswordFail() {
        val user = User(1L,name="test",age=10)
        val userPassword = "<PASSWORD>"
        val orgPassword = "TEST"
        assertFalse(user.validatePassword(userPassword,orgPassword))
    }

}