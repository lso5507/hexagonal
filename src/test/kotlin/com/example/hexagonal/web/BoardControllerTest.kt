package com.example.hexagonal.web

import com.example.hexagonal.HexagonalApplication
import com.example.hexagonal.domain.User
import com.example.hexagonal.domain.port.dto.BoardDto
import com.example.hexagonal.domain.port.dto.CreateBoardDto
import com.example.hexagonal.domain.port.dto.ModifyBoardDto
import com.example.hexagonal.domain.port.dto.UserCreateRequest
import com.example.hexagonal.persistence.UserAdapter
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(classes = [HexagonalApplication::class])
@AutoConfigureMockMvc
@Transactional
class BoardControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userAdapter: UserAdapter

    private lateinit var testUser: User

    @BeforeEach
    fun setUp() {
        // Create a test user before each test
        val userCreateRequest = UserCreateRequest(name = "testUser", email = "test@example.com")
        testUser = userAdapter.saveUser(userCreateRequest.toDomain())
    }

    @Nested
    @DisplayName("게시글 생성")
    inner class CreateBoardTests {
        @Test
        @DisplayName("성공: 유효한 데이터로 게시글 생성")
        fun `should create a board successfully`() {
            val createBoardDto = CreateBoardDto(
                title = "테스트 게시글",
                content = "테스트 내용입니다.",
                userId = testUser.id!!
            )

            mockMvc.perform(
                post("/boards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createBoardDto))
            )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").isNumber)
                .andExpect(jsonPath("$.title").value("테스트 게시글"))
                .andExpect(jsonPath("$.content").value("테스트 내용입니다."))
                .andExpect(jsonPath("$.userId").value(testUser.id))
        }

        @Test
        @DisplayName("실패: 제목이 없는 게시글 생성")
        fun `should return bad request when creating board with blank title`() {
            val createBoardDto = CreateBoardDto(
                title = "",
                content = "테스트 내용입니다.",
                userId = testUser.id!!
            )

            mockMvc.perform(
                post("/boards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createBoardDto))
            )
                .andExpect(status().isBadRequest)
        }

        @Test
        @DisplayName("실패: 내용이 없는 게시글 생성")
        fun `should return bad request when creating board with blank content`() {
            val createBoardDto = CreateBoardDto(
                title = "테스트 게시글",
                content = "",
                userId = testUser.id!!
            )

            mockMvc.perform(
                post("/boards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createBoardDto))
            )
                .andExpect(status().isBadRequest)
        }
    }

    @Nested
    @DisplayName("게시글 조회")
    inner class GetBoardTests {
        @Test
        @DisplayName("성공: ID로 게시글 조회")
        fun `should get board by id successfully`() {
            val createdBoard = createTestBoard(testUser.id!!)

            mockMvc.perform(get("/boards/${createdBoard.id}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(createdBoard.id))
                .andExpect(jsonPath("$.title").value(createdBoard.title))
                .andExpect(jsonPath("$.content").value(createdBoard.content))
                .andExpect(jsonPath("$.userId").value(createdBoard.userId))
        }

        @Test
        @DisplayName("예외: 존재하지 않는 게시글 조회")
        fun `should return not found when getting non-existent board`() {
            mockMvc.perform(get("/boards/99999"))
                .andExpect(status().isNotFound)
        }
    }

    @Nested
    @DisplayName("게시글 수정")
    inner class UpdateBoardTests {
        @Test
        @DisplayName("성공: 게시글 수정")
        fun `should update board successfully`() {
            val createdBoard = createTestBoard(testUser.id!!)
            val modifyBoardDto = ModifyBoardDto(
                id = createdBoard.id!!,
                title = "수정된 제목",
                content = "수정된 내용",
                userId = testUser.id!!
            )

            mockMvc.perform(
                put("/boards/${createdBoard.id}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(modifyBoardDto))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(createdBoard.id))
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.content").value("수정된 내용"))
                .andExpect(jsonPath("$.userId").value(testUser.id))
        }

        @Test
        @DisplayName("실패: 다른 사용자가 게시글 수정 시도")
        fun `should return bad request when another user tries to update board`() {
            val createdBoard = createTestBoard(testUser.id!!)
            val anotherUser = userAdapter.saveUser(UserCreateRequest(name = "anotherUser" + System.currentTimeMillis(), email = "another" + System.currentTimeMillis() + "@example.com").toDomain())

            val modifyBoardDto = ModifyBoardDto(
                id = createdBoard.id!!,
                title = "수정된 제목",
                content = "수정된 내용",
                userId = anotherUser.id!! // Mismatched user ID
            )

            mockMvc.perform(
                put("/boards/${createdBoard.id}")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(modifyBoardDto))
            )
                .andExpect(status().isBadRequest)
        }

        @Test
        @DisplayName("예외: 존재하지 않는 게시글 수정 시도")
        fun `should return not found when updating non-existent board`() {
            val modifyBoardDto = ModifyBoardDto(
                id = 99999L,
                title = "수정된 제목",
                content = "수정된 내용",
                userId = testUser.id!!
            )

            mockMvc.perform(
                put("/boards/99999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(modifyBoardDto))
            )
                .andExpect(status().isNotFound)
        }
    }

    @Nested
    @DisplayName("게시글 삭제")
    inner class DeleteBoardTests {
        @Test
        @DisplayName("성공: 게시글 삭제")
        fun `should delete board successfully`() {
            val createdBoard = createTestBoard(testUser.id!!)

            mockMvc.perform(delete("/boards/${createdBoard.id}"))
                .andExpect(status().isNoContent)

            // Verify it's actually deleted
            mockMvc.perform(get("/boards/${createdBoard.id}"))
                .andExpect(status().isNotFound)
        }

        @Test
        @DisplayName("예외: 존재하지 않는 게시글 삭제 시도")
        fun `should return not found when deleting non-existent board`() {
            mockMvc.perform(delete("/boards/99999"))
                .andExpect(status().isNotFound) // Or 204 No Content if delete is idempotent
        }
    }

    // Helper function to create a board for testing
    private fun createTestBoard(userId: Long): BoardDto {
        val createBoardDto = CreateBoardDto(
            title = "테스트 게시글",
            content = "테스트 내용입니다.",
            userId = userId
        )
        val result = mockMvc.perform(
            post("/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBoardDto))
        )
            .andExpect(status().isCreated)
            .andReturn()

        return objectMapper.readValue(result.response.contentAsString, BoardDto::class.java)
    }
}
