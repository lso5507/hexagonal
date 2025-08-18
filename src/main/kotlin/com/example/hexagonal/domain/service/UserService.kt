package com.example.hexagonal.domain.service

import com.example.hexagonal.domain.port.PasswordEncoderPort
import com.example.hexagonal.domain.port.UserAssembler
import com.example.hexagonal.domain.port.UserInPort
import com.example.hexagonal.domain.port.UserOutPort
import com.example.hexagonal.domain.port.dto.UserCreateRequest
import com.example.hexagonal.domain.port.dto.UserResponse
import com.example.hexagonal.domain.port.dto.UserUpdateRequest
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userOutport: UserOutPort,
    private val passwordEncoderPort: PasswordEncoderPort
) : UserInPort {
    override fun findUsers(id: Long): UserResponse {
        return userOutport.findUsers(id).let { UserAssembler.from(it) }
    }

    override fun createUser(userCreateRequest: UserCreateRequest): UserResponse {
        val user = UserAssembler.toCreate(userCreateRequest)
        user.password = passwordEncoderPort.encode(user.password)
        return UserAssembler.from(userOutport.saveUser(user))
    }

    override fun updateUser(userUpdateRequest: UserUpdateRequest) {
        UserAssembler.toUpdate(userUpdateRequest).let{
            userOutport.updateUser(it)
        }
    }

    override fun deleteUser(userIdToDelete: Long, authenticatedUserEmail: String) {
        val userToDelete = userOutport.findUsers(userIdToDelete)
        if (userToDelete.email != authenticatedUserEmail) {
            throw AccessDeniedException("You do not have permission to delete this user.")
        }
        userOutport.deleteUser(userIdToDelete)
    }
}