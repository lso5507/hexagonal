package com.example.hexagonal.domain.port

import com.example.hexagonal.domain.port.dto.ModifyDto
import com.example.hexagonal.domain.port.dto.SignupDto
import com.example.hexagonal.domain.port.dto.UserDto

interface UserInPort {
    fun findUsers(id:Long):UserDto
    fun createUser(signupDto: SignupDto): Boolean
    fun updateUser(modifyDto: ModifyDto): Boolean
    fun deleteUser(id: Long): Boolean
}