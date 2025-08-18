package com.example.hexagonal.domain.service

import com.example.hexagonal.BaseTest
import com.example.hexagonal.domain.User
import com.example.hexagonal.domain.port.dto.UserCreateRequest // Changed import
import com.example.hexagonal.domain.port.dto.UserUpdateRequest // Changed import
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserServiceTest: BaseTest() {
    @Autowired
    lateinit var userService: UserService
    lateinit var user: User

    @BeforeEach
    fun init(){
        user = initUser()
        entityManager.flush()
        entityManager.clear()
    }

    @Nested
    @DisplayName("Find User Scenarios")
    inner class FindUserTests {
        @Test
        @DisplayName("Should find a user by ID and return correct data")
        fun findUsers() {
            // when
            val findUser = userService.findUsers(user.id!!)
            // then
            assertEquals(user.id, findUser.id)
            assertEquals(user.name, findUser.name)
            assertEquals(user.email, findUser.email) // Changed from age to email
        }

        @Test
        @DisplayName("Should throw NoSuchElementException when user not found")
        fun findUser_notFound() {
            // given
            val nonExistentId = 9999L
            // when & then
            assertThrows(NoSuchElementException::class.java) {
                userService.findUsers(nonExistentId)
            }
        }
    }

    @Test
    @DisplayName("Should create a new user successfully")
    fun createUser() {
        // given
        val userCreateRequest = UserCreateRequest(name = "newUser", email = "newuser@example.com", password = "password") // Changed DTO and fields
        
        // when
        val createdUserDto = userService.createUser(userCreateRequest)
        entityManager.flush()
        entityManager.clear()

        // then
        assertNotNull(createdUserDto.id)
        assertEquals(userCreateRequest.name, createdUserDto.name)
        assertEquals(userCreateRequest.email, createdUserDto.email) // Changed from age to email

        val findUser = userService.findUsers(createdUserDto.id!!)
        assertEquals(createdUserDto.name, findUser.name)
        assertEquals(createdUserDto.email, findUser.email) // Added email assertion
    }

    @Test
    @DisplayName("Should update an existing user's name")
    fun updateUser() {
        // given
        val newName = "modified"
        val newEmail = "modified@example.com"
        val userUpdateRequest = UserUpdateRequest(id = user.id!!, name = newName, email = newEmail) // Changed DTO and fields
        
        // when
        userService.updateUser(userUpdateRequest)
        entityManager.flush()
        entityManager.clear()
        
        // then
        val updatedUser = userService.findUsers(user.id!!)
        assertEquals(newName, updatedUser.name)
        assertEquals(newEmail, updatedUser.email) // Added email assertion
    }

    @Nested
    @DisplayName("Delete User Scenarios")
    inner class DeleteUserTests {
        @Test
        @DisplayName("Should delete an existing user")
        fun deleteUser_success() {
            // given
            val userId = user.id!!
            assertDoesNotThrow { userService.findUsers(userId) } // Verify user exists

            // when
            userService.deleteUser(userId, user.email)
            entityManager.flush()
            entityManager.clear()

            // then
            assertThrows(NoSuchElementException::class.java) {
                userService.findUsers(userId)
            }
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when deleting another user")
        fun deleteUser_accessDenied() {
            // given
            val anotherUser = userAdapter.saveUser(User(name = "another", email = "another@example.com", password = "password"))
            val userIdToDelete = user.id!!

            // when & then
            assertThrows(org.springframework.security.access.AccessDeniedException::class.java) {
                userService.deleteUser(userIdToDelete, anotherUser.email)
            }
        }
    }
}
