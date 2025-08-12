package com.example.hexagonal

import com.example.hexagonal.domain.Board
import com.example.hexagonal.domain.User
import com.example.hexagonal.persistence.BoardAdapter
import com.example.hexagonal.persistence.UserAdapter
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@SpringBootTest
@Transactional
class BaseTest {
    @Autowired
    protected lateinit var entityManager: EntityManager

    @Autowired
    lateinit var userAdapter: UserAdapter
    @Autowired
    lateinit var boardAdapter: BoardAdapter
    fun initUser(): User {
        return userAdapter.saveUser(User(name = "test", age = 10))
    }
    fun initBoard(): Board {
        return boardAdapter.createBoard(Board(title="test", content = "content"))
    }
}