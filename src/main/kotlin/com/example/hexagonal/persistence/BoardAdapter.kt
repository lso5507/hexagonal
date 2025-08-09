package com.example.hexagonal.persistence

import com.example.hexagonal.domain.Board
import com.example.hexagonal.domain.port.BoardOutPort
import com.example.hexagonal.persistence.entity.toDomain
import com.example.hexagonal.persistence.entity.toEntity
import com.example.hexagonal.persistence.repository.BoardRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class BoardAdapter:BoardOutPort {
    @Autowired
    lateinit var boardRepository: BoardRepository
    override fun selectBoard(id: Long): Board {
        return boardRepository.findById(id)
            .map { it.toDomain }
            .orElseThrow { NoSuchElementException("Board with id $id not found") }
    }

    override fun createBoard(board: Board) {
        boardRepository.save(board.toEntity())
    }

    override fun modifyBoard(board: Board) {
        boardRepository.findById(board.id!!).get().let {
            it.title = board.title
            it.content = board.content
        }
    }

    override fun deleteBoard(id: Long) {
        //@TODO 세션 유저가 Board 도메인 작성자 유저인지 유효성검사필요
        boardRepository.deleteById(id)
    }


}