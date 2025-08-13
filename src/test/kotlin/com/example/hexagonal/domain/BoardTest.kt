package com.example.hexagonal.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BoardTest {
    @Test
    fun checkOwnerBoard(){
        val board = Board(1L,"test","test", userId = 1L) // Added userId
        val sessionUserId = 1L
        val requestUserId = 1L
        assertTrue(board.checkOwnerBoard(sessionUserId,requestUserId))
    }
}