package com.example.hexagonal.domain.service

import com.example.hexagonal.domain.port.UserAssembler
import com.example.hexagonal.domain.port.UserInPort
import com.example.hexagonal.domain.port.UserOutPort // Added import
import com.example.hexagonal.domain.port.dto.UserCreateRequest // Changed import
import com.example.hexagonal.domain.port.dto.UserUpdateRequest // Changed import
import com.example.hexagonal.domain.port.dto.UserResponse // Changed import
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userOutport: UserOutPort
) : UserInPort {
    override fun findUsers(id: Long): UserResponse { // Changed function name and return type
        val user = userOutport.findUsers(id) ?: throw RuntimeException("User not found") // Changed to findUsers
        return UserAssembler.from(user) // Changed to from
    }

    override fun createUser(userCreateRequest: UserCreateRequest): UserResponse { // Changed parameter and return type
        val user = UserAssembler.toCreate(userCreateRequest) // Changed to toCreate
        return UserAssembler.from(userOutport.saveUser(user)) // Changed to from
    }

    override fun updateUser(userUpdateRequest: UserUpdateRequest) { // Changed parameter and return type
        val user = UserAssembler.toUpdate(userUpdateRequest) // Changed to toUpdate
        userOutport.saveUser(user) // No return type for updateUser in UserInPort
    }

    override fun deleteUser(id: Long) { // Changed parameter name
        userOutport.deleteUser(id)
    }
}