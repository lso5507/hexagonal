package com.example.hexagonal.domain.port

import com.example.hexagonal.domain.User

interface UserOutport {
    fun findUsers(id:Long):User
    fun saveUser(user: User): Boolean
    fun deleteUser(id:Long): Boolean
    fun updateUser(user:User): Boolean
}