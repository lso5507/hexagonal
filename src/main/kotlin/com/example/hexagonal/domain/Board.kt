package com.example.hexagonal.domain

import java.time.LocalDateTime // Added import

data class Board(
    val id: Long? = null,
    val title: String,
    val content: String,
    val userId: Long, // Added userId
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    fun checkOwnerBoard(sessionUserId: Long,requestUserId: Long) = sessionUserId == requestUserId

}