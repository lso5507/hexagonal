package com.example.hexagonal.web

import com.example.hexagonal.domain.port.BoardInPort
import com.example.hexagonal.domain.port.UserInPort
import com.example.hexagonal.domain.port.dto.ModifyBoardDto
import com.example.hexagonal.domain.port.dto.ModifyDto
import com.example.hexagonal.domain.port.dto.SignupDto
import com.example.hexagonal.domain.port.dto.CreateBoardDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*


@Controller
@RequestMapping("/api/v1/boards")
class BoardController {
    @Autowired
    lateinit var boardInport: BoardInPort

    @PostMapping
    fun createBoard(@RequestBody createBoardDto: CreateBoardDto) = boardInport.createBoard(createBoardDto)
    @PutMapping
    fun updateBoard(@RequestBody modifyBoardDto: ModifyBoardDto) = boardInport.modifyBoard(modifyBoardDto)
    @DeleteMapping("{id}")
    fun deleteBoard(@PathVariable id:Long) = boardInport.deleteBoard(id)
}