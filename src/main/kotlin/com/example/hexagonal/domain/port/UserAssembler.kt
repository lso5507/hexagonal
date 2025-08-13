package com.example.hexagonal.domain.port

import com.example.hexagonal.domain.User
import com.example.hexagonal.domain.port.dto.UserCreateRequest // Changed import
import com.example.hexagonal.domain.port.dto.UserUpdateRequest // Changed import
import com.example.hexagonal.domain.port.dto.UserResponse // Changed import

class UserAssembler {
    companion object{
        fun toCreate(userCreateRequest: UserCreateRequest): User{ // Changed parameter type
            return User(
                name = userCreateRequest.name,
                email = userCreateRequest.email
            )
        }
        fun toUpdate(userUpdateRequest: UserUpdateRequest): User{ // Changed parameter type
            return User(
                id = userUpdateRequest.id, // Assuming id is passed in UserUpdateRequest
                name = userUpdateRequest.name,
                email = userUpdateRequest.email
            )
        }
        fun from(user: User): UserResponse{ // Changed return type
            return UserResponse(
                id = user.id!!,
                name = user.name,
                email = user.email,
                createdAt = user.createdAt!!,
                updatedAt = user.updatedAt!!
            )
        }
    }
}