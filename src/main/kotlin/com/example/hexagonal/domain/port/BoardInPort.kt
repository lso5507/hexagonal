package com.example.hexagonal.domain.port

import com.example.hexagonal.domain.Board
import com.example.hexagonal.domain.port.dto.BoardDto
import com.example.hexagonal.domain.port.dto.ModifyBoardDto
import com.example.hexagonal.domain.port.dto.CreateBoardDto

interface BoardInPort {
    fun selectBoard(id:Long): BoardDto
    fun createBoard(createBoardDto: CreateBoardDto)
    fun modifyBoard(modifyBoardDto: ModifyBoardDto)
    fun deleteBoard(id: Long)
}