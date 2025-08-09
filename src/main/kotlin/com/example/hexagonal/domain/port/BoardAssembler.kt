package com.example.hexagonal.domain.port

import com.example.hexagonal.domain.Board
import com.example.hexagonal.domain.port.dto.ModifyBoardDto
import com.example.hexagonal.domain.port.dto.createBoardDto

class BoardAssembler {
    companion object{
        fun toCreate(createBoardDto: createBoardDto):Board=
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
    }
}