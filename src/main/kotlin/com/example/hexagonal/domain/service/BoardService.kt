package com.example.hexagonal.domain.service

import com.example.hexagonal.domain.port.BoardAssembler
import com.example.hexagonal.domain.port.BoardInPort
import com.example.hexagonal.domain.port.BoardOutPort
import com.example.hexagonal.domain.port.dto.ModifyBoardDto
import com.example.hexagonal.domain.port.dto.createBoardDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BoardService:BoardInPort {
    @Autowired
    lateinit var boardOutPort: BoardOutPort
    override fun createBoard(createBoardDto: createBoardDto) {
        boardOutPort.createBoard(BoardAssembler.toCreate(createBoardDto))
    }

    override fun modifyBoard(modifyBoardDto: ModifyBoardDto) {
        boardOutPort.modifyBoard(BoardAssembler.toModify(modifyBoardDto))
    }

    override fun deleteBoard(id: Long) {

        boardOutPort.deleteBoard(id)
    }
}