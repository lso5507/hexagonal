package com.example.hexagonal.domain.port

import com.example.hexagonal.domain.Board
import com.example.hexagonal.domain.port.dto.ModifyBoardDto
import com.example.hexagonal.domain.port.dto.createBoardDto

interface BoardOutPort{
    fun selectBoard(id: Long): Board
    fun createBoard(board: Board)
    fun modifyBoard(board: Board)
    fun deleteBoard(id: Long)
}