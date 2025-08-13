package com.example.hexagonal.domain.port

import com.example.hexagonal.domain.Board
import com.example.hexagonal.domain.port.dto.BoardDto
import com.example.hexagonal.domain.port.dto.ModifyBoardDto
import com.example.hexagonal.domain.port.dto.CreateBoardDto

interface BoardInPort {
    fun getBoard(boardId: Long): BoardDto // Changed name and parameter
    fun createBoard(createBoardDto: CreateBoardDto): BoardDto // Changed return type
    fun updateBoard(boardId: Long, modifyBoardDto: ModifyBoardDto): BoardDto // Changed name and parameters
    fun deleteBoard(boardId: Long) // Changed parameter name
}