package com.example.hexagonal.domain.service

import com.example.hexagonal.domain.port.BoardInPort
import com.example.hexagonal.domain.port.BoardOutPort
import com.example.hexagonal.domain.port.dto.BoardDto
import com.example.hexagonal.domain.port.dto.CreateBoardDto
import com.example.hexagonal.domain.port.dto.ModifyBoardDto
import org.springframework.http.HttpStatus // Import HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException // Import ResponseStatusException

@Service
class BoardService(
    private val boardOutPort: BoardOutPort
) : BoardInPort {
    override fun createBoard(createBoardDto: CreateBoardDto): BoardDto {
        return createBoardDto.toDomain().let{
            BoardDto.fromDomain(boardOutPort.saveBoard(it))
        }
    }

    override fun getBoard(boardId: Long): BoardDto {
        return boardOutPort.findBoardById(boardId)?.let(BoardDto::fromDomain)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found")
    }

    override fun updateBoard(boardId: Long, modifyBoardDto: ModifyBoardDto): BoardDto {
        return boardOutPort.findBoardById(boardId)?.let{
            if (it.userId != modifyBoardDto.userId)
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID mismatch. You can only modify your own board.")

            modifyBoardDto.toDomain().copy(id = boardId).let{
                BoardDto.fromDomain(boardOutPort.updateBoard(it))
            }
        }?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found")
    }

    override fun deleteBoard(boardId: Long) {
        boardOutPort.findBoardById(boardId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found")
        boardOutPort.deleteBoard(boardId)
    }
}