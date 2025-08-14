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
        return userOutport.findUsers(id).let { UserAssembler.from(it) }
    }

    override fun createUser(userCreateRequest: UserCreateRequest): UserResponse { // Changed parameter and return type
        return UserAssembler.toCreate(userCreateRequest).let{
            UserAssembler.from(userOutport.saveUser(it))
        }
    }

    override fun updateUser(userUpdateRequest: UserUpdateRequest) { // Changed parameter and return type
        UserAssembler.toUpdate(userUpdateRequest).let{
            userOutport.updateUser(it)
        }
    }

    override fun deleteUser(id: Long) { // Changed parameter name
        userOutport.deleteUser(id)
    }
}