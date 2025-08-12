package com.example.hexagonal.domain.service

import com.example.hexagonal.domain.port.BoardAssembler
import com.example.hexagonal.domain.port.BoardInPort
import com.example.hexagonal.domain.port.BoardOutPort
import com.example.hexagonal.domain.port.dto.BoardDto
import com.example.hexagonal.domain.port.dto.ModifyBoardDto
import com.example.hexagonal.domain.port.dto.CreateBoardDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BoardService:BoardInPort {
    @Autowired
    lateinit var boardOutPort: BoardOutPort
    override fun selectBoard(id: Long): BoardDto {
        boardOutPort.selectBoard(id).run{
            return BoardAssembler.from(this)
        }
    }

    override fun createBoard(createBoardDto: CreateBoardDto) {
        boardOutPort.createBoard(BoardAssembler.toCreate(createBoardDto))
    }

    override fun modifyBoard(modifyBoardDto: ModifyBoardDto) {
        boardOutPort.modifyBoard(BoardAssembler.toModify(modifyBoardDto))
    }

    override fun deleteBoard(id: Long) {
        val selectBoard = boardOutPort.selectBoard(id)
        //@TODO owner ID 체크
        boardOutPort.deleteBoard(id)
    }
}