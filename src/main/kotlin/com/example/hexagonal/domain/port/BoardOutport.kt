package com.example.hexagonal.domain.port

import com.example.hexagonal.domain.Board

interface BoardOutPort{
    fun selectBoard(id: Long): Board
    fun createBoard(board: Board): Board
    fun modifyBoard(board: Board)
    fun deleteBoard(id: Long)
}