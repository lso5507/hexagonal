package com.example.hexagonal.persistence

import com.example.hexagonal.domain.Board
import com.example.hexagonal.domain.port.BoardOutport // Added import
import com.example.hexagonal.persistence.entity.BoardEntity
import com.example.hexagonal.persistence.entity.toDomain
import com.example.hexagonal.persistence.entity.toEntity
import com.example.hexagonal.persistence.repository.BoardRepository
import com.example.hexagonal.persistence.repository.UserRepository // Added UserRepository import
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class BoardAdapter(
    private val boardRepository: BoardRepository,
    private val userRepository: UserRepository // Injected UserRepository
) : BoardOutport {
    override fun saveBoard(board: Board): Board {
        val userEntity = userRepository.findByIdOrNull(board.userId)
            ?: throw IllegalArgumentException("User not found with ID: ${board.userId}") // Fetch UserEntity
        val boardEntity = board.toEntity(userEntity) // Pass UserEntity to toEntity
        return boardRepository.save(boardEntity).toDomain
    }

    override fun findBoardById(boardId: Long): Board? {
        return boardRepository.findByIdOrNull(boardId)?.toDomain
    }

    override fun deleteBoard(boardId: Long) {
        boardRepository.deleteById(boardId)
    }

    // Assuming updateBoard will be called from BoardService
    override fun updateBoard(board: Board): Board {
        val existingBoardEntity = boardRepository.findByIdOrNull(board.id!!)
            ?: throw IllegalArgumentException("Board not found with ID: ${board.id}")

        val userEntity = userRepository.findByIdOrNull(board.userId)
            ?: throw IllegalArgumentException("User not found with ID: ${board.userId}")

        existingBoardEntity.title = board.title
        existingBoardEntity.content = board.content
        existingBoardEntity.user = userEntity // Update user
        // createdAt is val, updatedAt is var and will be updated by JPA automatically or can be set manually

        return boardRepository.save(existingBoardEntity).toDomain
    }
}