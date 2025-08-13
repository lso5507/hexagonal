package com.example.hexagonal.persistence.entity

import com.example.hexagonal.domain.Board
import com.example.hexagonal.domain.User // Added import for User


val BoardEntity.toDomain:Board
    get() = Board(
        id = this@toDomain.id,
        title = this@toDomain.title,
        content = this@toDomain.content,
        userId = this@toDomain.user.id!! // Added userId
    )

// Modified to accept UserEntity for the relationship
fun Board.toEntity(userEntity: UserEntity): BoardEntity =
    BoardEntity(
        id = this.id,
        title = this.title,
        content = this.content,
        user = userEntity
    )
