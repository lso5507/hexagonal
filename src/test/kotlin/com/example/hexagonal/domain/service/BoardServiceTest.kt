package com.example.hexagonal.domain.service

import com.example.hexagonal.BaseTest
import com.example.hexagonal.domain.Board
import com.example.hexagonal.domain.User
import com.example.hexagonal.domain.port.dto.CreateBoardDto
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class BoardServiceTest: BaseTest() {
    @Autowired
    lateinit var boardService: BoardService
    lateinit var board: Board
    @BeforeEach
    fun init(){
         board = initBoard()
    }
    @Test
    fun selectBoard() {
        boardService.selectBoard(board.id!!)
    }

    @Test
    fun createBoard() {
        boardService.createBoard(CreateBoardDto(title = "test", content = "content"))
    }

    @Test
    fun modifyBoard() {
    }

    @Test
    fun deleteBoard() {
    }

}