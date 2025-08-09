package com.example.hexagonal.domain.port

import com.example.hexagonal.domain.Board
import com.example.hexagonal.domain.port.dto.ModifyBoardDto
import com.example.hexagonal.domain.port.dto.createBoardDto

interface BoardInPort {
    fun createBoard(createBoardDto: createBoardDto)
    fun modifyBoard(modifyBoardDto: ModifyBoardDto)
    fun deleteBoard(id: Long)
}