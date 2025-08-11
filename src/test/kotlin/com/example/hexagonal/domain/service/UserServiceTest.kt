package com.example.hexagonal.domain.service

import com.example.hexagonal.BaseTest
import com.example.hexagonal.domain.User
import com.example.hexagonal.domain.port.dto.ModifyDto
import com.example.hexagonal.domain.port.dto.SignupDto
import org.aspectj.lang.annotation.Before
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

class UserServiceTest: BaseTest() {
    @Autowired
    lateinit var userService: UserService
    lateinit var user: User
    @BeforeEach
    fun init(){
        user = initUser()
    }
    @Test
    fun findUsers() {
        //given
        //when
        val findUsers = userService.findUsers(user.id!!)
        //then
        assertEquals(findUsers.id,user.id!!)
    }

    @Test
    fun createUser() {
        //given
        //when
        //then
        assertDoesNotThrow {  userService.createUser(SignupDto(name = "test", age = 10))}
    }

    @Test
    fun updateUser() {
        //given
        //when
        userService.updateUser(ModifyDto(id = 1L, name = "modify"))
        entityManager.flush()
        entityManager.clear()
        val findUsers = userService.findUsers(1L)
        //then
        assertEquals(findUsers.name,"modify")

    }

    @Test
    fun deleteUser() {
        //given
        //when
        userService.deleteUser(1L)
        entityManager.flush()
        entityManager.clear()
        assertThrows(NoSuchElementException::class.java){ userService.findUsers(1L)}

    }

}