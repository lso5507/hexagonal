package com.example.hexagonal.persistence.entity

import com.example.hexagonal.domain.Board


val BoardEntity.toDomain:Board
    get() = Board(
        id = this@toDomain.id,
        title = this@toDomain.title,
        content = this@toDomain.content
    )

fun Board.toEntity(): BoardEntity =
    BoardEntity().apply {
        title = this@toEntity.title
        content = this@toEntity.content
    }
