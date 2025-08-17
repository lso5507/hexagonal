package com.example.hexagonal.domain.service

import com.example.hexagonal.domain.Board
import com.example.hexagonal.domain.port.BoardOutPort
import com.example.hexagonal.domain.port.dto.CreateBoardDto
import com.example.hexagonal.domain.port.dto.ModifyBoardDto
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows // Import assertThrows
import org.springframework.http.HttpStatus // Import HttpStatus
import org.springframework.web.server.ResponseStatusException // Import ResponseStatusException
import java.time.LocalDateTime

class BoardServiceTest {

    private lateinit var boardOutport: BoardOutPort
    private lateinit var boardService: BoardService

    @BeforeEach
    fun setUp() {
        boardOutport = mockk()
        boardService = BoardService(boardOutport)
    }

    @Test
    fun `createBoard should save and return board`() {
        // Given
        val userId = 1L
        val createBoardDto = CreateBoardDto(title = "Test Title", content = "Test Content", userId = userId)
        val board = Board(id = 1L, title = "Test Title", content = "Test Content", userId = userId, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        every { boardOutport.saveBoard(any()) } returns board

        // When
        val result = boardService.createBoard(createBoardDto)

        // Then
        assertNotNull(result)
        assertEquals(board.id, result.id)
        assertEquals(board.title, result.title)
        assertEquals(board.content, result.content)
        assertEquals(board.userId, result.userId) // Assert userId
        verify(exactly = 1) { boardOutport.saveBoard(any()) }
    }

    @Test
    fun `getBoard should return board by id`() {
        // Given
        val boardId = 1L
        val userId = 1L
        val board = Board(id = boardId, title = "Test Title", content = "Test Content", userId = userId, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        every { boardOutport.findBoardById(boardId) } returns board

        // When
        val result = boardService.getBoard(boardId)

        // Then
        assertNotNull(result)
        assertEquals(board.id, result.id)
        assertEquals(board.userId, result.userId) // Assert userId
        verify(exactly = 1) { boardOutport.findBoardById(boardId) }
    }

    @Test
    fun `updateBoard should update and return board when user IDs match`() {
        // Given
        val boardId = 1L
        val userId = 1L
        val modifyBoardDto = ModifyBoardDto(id = boardId, title = "Updated Title", content = "Updated Content", userId = userId)
        val existingBoard = Board(id = boardId, title = "Old Title", content = "Old Content", userId = userId, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())
        val updatedBoard = existingBoard.copy(title = "Updated Title", content = "Updated Content")

        every { boardOutport.findBoardById(boardId) } returns existingBoard
        every { boardOutport.updateBoard(any()) } returns updatedBoard // Changed to updateBoard

        // When
        val result = boardService.updateBoard(boardId, modifyBoardDto)

        // Then
        assertNotNull(result)
        assertEquals(updatedBoard.id, result.id)
        assertEquals(updatedBoard.title, result.title)
        assertEquals(updatedBoard.content, result.content)
        assertEquals(updatedBoard.userId, result.userId) // Assert userId
        verify(exactly = 1) { boardOutport.findBoardById(boardId) }
        verify(exactly = 1) { boardOutport.updateBoard(any()) } // Changed to updateBoard
    }

    @Test
    fun `updateBoard should throw ResponseStatusException when user IDs do not match`() {
        // Given
        val boardId = 1L
        val existingUserId = 1L
        val requestUserId = 2L // Different user ID
        val modifyBoardDto = ModifyBoardDto(id = boardId, title = "Updated Title", content = "Updated Content", userId = requestUserId)
        val existingBoard = Board(id = boardId, title = "Old Title", content = "Old Content", userId = existingUserId, createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now())

        every { boardOutport.findBoardById(boardId) } returns existingBoard

        // When & Then
        val exception = assertThrows<ResponseStatusException> {
            boardService.updateBoard(boardId, modifyBoardDto)
        }
        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
        assertEquals("User ID mismatch. You can only modify your own board.", exception.reason)
        verify(exactly = 1) { boardOutport.findBoardById(boardId) }
        verify(exactly = 0) { boardOutport.updateBoard(any()) } // Ensure updateBoard is not called
    }

    @Test
    fun `deleteBoard should delete board by id`() {
        // Given
        val boardId = 1L
        val userId = 1L // Assuming a user exists for the board
        val board = Board(id = boardId, title = "Test", content = "Test", userId = userId) // Create a dummy board
        every { boardOutport.findBoardById(boardId) } returns board // Mock findBoardById
        every { boardOutport.deleteBoard(boardId) } returns Unit

        // When
        boardService.deleteBoard(boardId)

        // Then
        verify(exactly = 1) { boardOutport.findBoardById(boardId) } // Verify findBoardById was called
        verify(exactly = 1) { boardOutport.deleteBoard(boardId) }
    }
}
