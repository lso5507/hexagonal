package com.example.hexagonal.domain.port

import com.example.hexagonal.domain.User

interface UserOutPort {
    fun findUsers(id:Long):User
    fun saveUser(user: User): User
    fun deleteUser(id:Long)
    fun updateUser(user:User)
}