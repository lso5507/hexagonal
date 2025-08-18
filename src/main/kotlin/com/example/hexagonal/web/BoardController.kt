package com.example.hexagonal.web

import com.example.hexagonal.domain.port.BoardInPort
import com.example.hexagonal.domain.port.dto.BoardDto // Added import
import com.example.hexagonal.domain.port.dto.CreateBoardDto
import com.example.hexagonal.domain.port.dto.ModifyBoardDto
import jakarta.validation.Valid // Added import
import org.springframework.http.HttpStatus // Added import
import org.springframework.http.ResponseEntity // Added import
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
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
    fun updateBoard(
        @PathVariable boardId: Long,
        @Valid @RequestBody modifyBoardDto: ModifyBoardDto,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<BoardDto> { // Added @Valid
        val updatedBoard = boardInPort.updateBoard(boardId, modifyBoardDto, userDetails.username)
        return ResponseEntity.ok(updatedBoard)
    }

    @DeleteMapping("/{boardId}") // Changed DeleteMapping and added path variable
    fun deleteBoard(@PathVariable boardId: Long, @AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<Void> { // Changed method signature
        boardInPort.deleteBoard(boardId, userDetails.username)
        return ResponseEntity.noContent().build()
    }
}