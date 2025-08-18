package com.example.hexagonal.persistence.entity

import com.example.hexagonal.domain.User
import java.time.LocalDateTime // Added import

val UserEntity.toDomain: User
    get() = User(
        id = this.id,
        name = this.name?:
"",
        email = this.email?:
"",
        password = this.password,
        roles = this.roles.split(',').toMutableList(),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
fun User.toEntity(): UserEntity=
    UserEntity(
        id = this.id, // Pass nullable id directly
        name = this.name,
        email = this.email,
        password = this.password,
        roles = this.roles.joinToString(","),
        createdAt = this.createdAt ?: LocalDateTime.now(),
        updatedAt = this.updatedAt ?: LocalDateTime.now()
    )