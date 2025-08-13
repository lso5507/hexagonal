package com.example.hexagonal.domain.port

import com.example.hexagonal.domain.User
import com.example.hexagonal.domain.port.dto.UserCreateRequest // Changed import
import com.example.hexagonal.domain.port.dto.UserUpdateRequest // Changed import
import com.example.hexagonal.domain.port.dto.UserResponse // Changed import

interface UserInPort {
    fun findUsers(id:Long):UserResponse // Changed return type
    fun createUser(userCreateRequest: UserCreateRequest):UserResponse // Changed parameter and return type
    fun updateUser(userUpdateRequest: UserUpdateRequest) // Changed parameter
    fun deleteUser(id: Long)
}