package com.example.hexagonal.domain.port

import com.example.hexagonal.domain.Board
import com.example.hexagonal.domain.port.dto.BoardDto
import com.example.hexagonal.domain.port.dto.ModifyBoardDto
import com.example.hexagonal.domain.port.dto.CreateBoardDto

class BoardAssembler {
    companion object{
        fun toCreate(createBoardDto: CreateBoardDto):Board=
            Board(
                title = createBoardDto.title,
                content = createBoardDto.content
            )
        fun toModify(modifyBoardDto: ModifyBoardDto):Board=
            Board(
                id = modifyBoardDto.id,
                title = modifyBoardDto.title,
                content = modifyBoardDto.content
            )
        fun from(board: Board): BoardDto {
            return BoardDto(
                id = board.id!!,
                title = board.title,
                content = board.content
            )
        }
    }
}