package com.example.hexagonal

import com.example.hexagonal.domain.User
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
    fun initUser(): User {
        return userAdapter.saveUser(User(name = "test", age = 10))
    }
}