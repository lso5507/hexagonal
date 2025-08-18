package com.example.hexagonal.domain.service

import com.example.hexagonal.domain.Board
import com.example.hexagonal.domain.User
import com.example.hexagonal.domain.port.BoardOutPort
import com.example.hexagonal.domain.port.UserOutPort
import com.example.hexagonal.domain.port.dto.CreateBoardDto
import com.example.hexagonal.domain.port.dto.ModifyBoardDto
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.access.AccessDeniedException
import java.time.LocalDateTime

class BoardServiceTest {

    private lateinit var boardOutPort: BoardOutPort
    private lateinit var userOutPort: UserOutPort
    private lateinit var boardService: BoardService

    @BeforeEach
    fun setUp() {
        boardOutPort = mockk()
        userOutPort = mockk()
        boardService = BoardService(boardOutPort, userOutPort)
    }

    @Test
    fun `createBoard should save and return board`() {
        // Given
        val userId = 1L
        val createBoardDto = CreateBoardDto(title = "Test Title", content = "Test Content", userId = userId)
        val board = Board(id = 1L, title = "Test Title", content = "Test Content", userId = userId, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        every { boardOutPort.saveBoard(any()) } returns board

        // When
        val result = boardService.createBoard(createBoardDto)

        // Then
        assertNotNull(result)
        assertEquals(board.id, result.id)
        verify(exactly = 1) { boardOutPort.saveBoard(any()) }
    }

    @Test
    fun `updateBoard should update board if user is the owner`() {
        // Given
        val boardId = 1L
        val ownerEmail = "owner@test.com"
        val owner = User(id = 1L, name = "owner", email = ownerEmail, password = "password")
        val existingBoard = Board(id = boardId, title = "Old Title", content = "Old Content", userId = owner.id!!)
        val modifyBoardDto = ModifyBoardDto(id = boardId, title = "New Title", content = "New Content")

        every { boardOutPort.findBoardById(boardId) } returns existingBoard
        every { userOutPort.findUsers(owner.id!!) } returns owner
        every { boardOutPort.updateBoard(any()) } answers { firstArg() }

        // When
        val result = boardService.updateBoard(boardId, modifyBoardDto, ownerEmail)

        // Then
        assertEquals("New Title", result.title)
        verify(exactly = 1) { boardOutPort.updateBoard(any()) }
    }

    @Test
    fun `updateBoard should throw AccessDeniedException if user is not the owner`() {
        // Given
        val boardId = 1L
        val ownerEmail = "owner@test.com"
        val attackerEmail = "attacker@test.com"
        val owner = User(id = 1L, name = "owner", email = ownerEmail, password = "password")
        val existingBoard = Board(id = boardId, title = "Old Title", content = "Old Content", userId = owner.id!!)
        val modifyBoardDto = ModifyBoardDto(id = boardId, title = "New Title", content = "New Content")

        every { boardOutPort.findBoardById(boardId) } returns existingBoard
        every { userOutPort.findUsers(owner.id!!) } returns owner

        // When & Then
        assertThrows<AccessDeniedException> {
            boardService.updateBoard(boardId, modifyBoardDto, attackerEmail)
        }
        verify(exactly = 0) { boardOutPort.updateBoard(any()) }
    }

    @Test
    fun `deleteBoard should delete board if user is the owner`() {
        // Given
        val boardId = 1L
        val ownerEmail = "owner@test.com"
        val owner = User(id = 1L, name = "owner", email = ownerEmail, password = "password")
        val existingBoard = Board(id = boardId, title = "Title", content = "Content", userId = owner.id!!)

        every { boardOutPort.findBoardById(boardId) } returns existingBoard
        every { userOutPort.findUsers(owner.id!!) } returns owner
        every { boardOutPort.deleteBoard(boardId) } returns Unit

        // When
        boardService.deleteBoard(boardId, ownerEmail)

        // Then
        verify(exactly = 1) { boardOutPort.deleteBoard(boardId) }
    }

    @Test
    fun `deleteBoard should throw AccessDeniedException if user is not the owner`() {
        // Given
        val boardId = 1L
        val ownerEmail = "owner@test.com"
        val attackerEmail = "attacker@test.com"
        val owner = User(id = 1L, name = "owner", email = ownerEmail, password = "password")
        val existingBoard = Board(id = boardId, title = "Title", content = "Content", userId = owner.id!!)

        every { boardOutPort.findBoardById(boardId) } returns existingBoard
        every { userOutPort.findUsers(owner.id!!) } returns owner

        // When & Then
        assertThrows<AccessDeniedException> {
            boardService.deleteBoard(boardId, attackerEmail)
        }
        verify(exactly = 0) { boardOutPort.deleteBoard(any()) }
    }
}