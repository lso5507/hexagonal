package com.example.hexagonal.web

import com.example.hexagonal.domain.port.BoardInPort
import com.example.hexagonal.domain.port.dto.BoardDto // Added import
import com.example.hexagonal.domain.port.dto.CreateBoardDto
import com.example.hexagonal.domain.port.dto.ModifyBoardDto
import jakarta.validation.Valid // Added import
import org.springframework.http.HttpStatus // Added import
import org.springframework.http.ResponseEntity // Added import
import org.springframework.web.bind.annotation.*

import org.springframework.validation.annotation.Validated // Added import

@RestController // Changed to RestController
@RequestMapping("/boards") // Changed mapping
@Validated // Added @Validated
class BoardController(
    private val boardInPort: BoardInPort // Changed to constructor injection
) {
    @PostMapping
    fun createBoard(@Valid @RequestBody createBoardDto: CreateBoardDto): ResponseEntity<BoardDto> { // Added @Valid
        val createdBoard = boardInPort.createBoard(createBoardDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBoard)
    }

    @GetMapping("/{boardId}") // Added GetMapping
    fun getBoard(@PathVariable boardId: Long): ResponseEntity<BoardDto> { // Added getBoard
        val board = boardInPort.getBoard(boardId)
        return ResponseEntity.ok(board)
    }

    @PutMapping("/{boardId}") // Changed PutMapping and added path variable
    fun updateBoard(@PathVariable boardId: Long, @Valid @RequestBody modifyBoardDto: ModifyBoardDto): ResponseEntity<BoardDto> { // Added @Valid
        val updatedBoard = boardInPort.updateBoard(boardId, modifyBoardDto)
        return ResponseEntity.ok(updatedBoard)
    }

    @DeleteMapping("/{boardId}") // Changed DeleteMapping and added path variable
    fun deleteBoard(@PathVariable boardId: Long): ResponseEntity<Void> { // Changed method signature
        boardInPort.deleteBoard(boardId)
        return ResponseEntity.noContent().build()
    }
}