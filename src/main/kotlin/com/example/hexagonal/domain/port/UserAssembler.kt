package com.example.hexagonal.domain.port

import com.example.hexagonal.domain.User
import com.example.hexagonal.domain.port.dto.ModifyDto
import com.example.hexagonal.domain.port.dto.SignupDto
import com.example.hexagonal.domain.port.dto.UserDto

class UserAssembler {
    companion object{
        fun toCreate(signupDto: SignupDto): User{
            return User(
                id=null,
                name = signupDto.name,
                age=signupDto.age
            )
        }
        fun toUpdate(modifyDto: ModifyDto): User{
            return User(
                id=modifyDto.id,
                name = modifyDto.name,
                age=-1
            )
        }
        fun from(user: User): UserDto{
            return UserDto(
                id = user.id!!,
                name = user.name,
                age = user.age
            )
        }
    }
}