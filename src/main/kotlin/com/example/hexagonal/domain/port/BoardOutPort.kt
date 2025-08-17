package com.example.hexagonal.domain.port

import com.example.hexagonal.domain.Board

interface BoardOutPort{ // Changed name to BoardOutport (capital 'P')
    fun findBoardById(boardId: Long): Board? // Changed name and return type
    fun saveBoard(board: Board): Board // Changed name
    fun updateBoard(board: Board): Board // Changed name and return type
    fun deleteBoard(boardId: Long) // Changed parameter name
}