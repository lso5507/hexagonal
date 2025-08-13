package com.example.hexagonal.domain.service

import com.example.hexagonal.domain.Board
import com.example.hexagonal.domain.port.BoardInPort
import com.example.hexagonal.domain.port.BoardOutport
import com.example.hexagonal.domain.port.dto.BoardDto
import com.example.hexagonal.domain.port.dto.CreateBoardDto
import com.example.hexagonal.domain.port.dto.ModifyBoardDto
import org.springframework.http.HttpStatus // Import HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException // Import ResponseStatusException

@Service
class BoardService(
    private val boardOutport: BoardOutport
) : BoardInPort {
    override fun createBoard(createBoardDto: CreateBoardDto): BoardDto {
        val board = createBoardDto.toDomain()
        return BoardDto.fromDomain(boardOutport.saveBoard(board))
    }

    override fun getBoard(boardId: Long): BoardDto {
        val board = boardOutport.findBoardById(boardId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found") // Changed exception
        return BoardDto.fromDomain(board)
    }

    override fun updateBoard(boardId: Long, modifyBoardDto: ModifyBoardDto): BoardDto {
        val existingBoard = boardOutport.findBoardById(boardId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found") // Changed exception

        // User ID validation
        if (existingBoard.userId != modifyBoardDto.userId) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID mismatch. You can only modify your own board.")
        }

        val updatedBoard = modifyBoardDto.toDomain().copy(id = boardId) // Ensure ID is set for update
        return BoardDto.fromDomain(boardOutport.updateBoard(updatedBoard)) // Changed to updateBoard
    }

    override fun deleteBoard(boardId: Long) {
        boardOutport.findBoardById(boardId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found")
        boardOutport.deleteBoard(boardId)
    }
}