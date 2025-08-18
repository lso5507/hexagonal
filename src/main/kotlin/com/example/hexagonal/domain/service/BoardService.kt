package com.example.hexagonal.domain.service

import com.example.hexagonal.domain.port.BoardInPort
import com.example.hexagonal.domain.port.BoardOutPort
import com.example.hexagonal.domain.port.UserOutPort
import com.example.hexagonal.domain.port.dto.BoardDto
import com.example.hexagonal.domain.port.dto.CreateBoardDto
import com.example.hexagonal.domain.port.dto.ModifyBoardDto
import org.springframework.http.HttpStatus // Import HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException // Import ResponseStatusException

@Service
class BoardService(
    private val boardOutPort: BoardOutPort,
    private val userOutPort: UserOutPort
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

    override fun updateBoard(boardId: Long, modifyBoardDto: ModifyBoardDto, authenticatedUserEmail: String): BoardDto {
        val originalBoard = boardOutPort.findBoardById(boardId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found")

        val boardOwner = userOutPort.findUsers(originalBoard.userId)
        if (boardOwner.email != authenticatedUserEmail) {
            throw AccessDeniedException("You do not have permission to modify this board.")
        }

        val boardToUpdate = modifyBoardDto.toDomain().copy(
            id = boardId,
            userId = originalBoard.userId // Preserve original userId
        )
        return BoardDto.fromDomain(boardOutPort.updateBoard(boardToUpdate))
    }

    override fun deleteBoard(boardId: Long, authenticatedUserEmail: String) {
        val originalBoard = boardOutPort.findBoardById(boardId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found")

        val boardOwner = userOutPort.findUsers(originalBoard.userId)
        if (boardOwner.email != authenticatedUserEmail) {
            throw AccessDeniedException("You do not have permission to delete this board.")
        }

        boardOutPort.deleteBoard(boardId)
    }
}