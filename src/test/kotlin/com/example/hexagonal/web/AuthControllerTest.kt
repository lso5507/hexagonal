package com.example.hexagonal.web

import com.example.hexagonal.domain.port.dto.LoginRequest
import com.example.hexagonal.domain.port.dto.UserCreateRequest
import com.example.hexagonal.domain.port.dto.UserResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper
) {

    private val email = "test@test.com"
    private val password = "password123"
    private lateinit var testUser: UserResponse

    @BeforeEach
    fun setUp() {
        // 회원가입
        val signUpRequest = UserCreateRequest(name = "testuser", email = email, password = password)
        val result = mockMvc.post("/users") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(signUpRequest)
        }.andExpect {
            status {
                isCreated()
            }
        }.andReturn()

        testUser = objectMapper.readValue(result.response.contentAsString, UserResponse::class.java)
    }

    @Test
    fun `로그인 성공 테스트`() {
        val loginRequest = LoginRequest(email = email, password = password)

        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { exists() }
        }
    }

    @Test
    fun `잘못된 비밀번호로 로그인 실패 테스트`() {
        val loginRequest = LoginRequest(email = email, password = "wrongpassword")

        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `토큰 없이 보호된 API 접근 실패 테스트`() {
        mockMvc.get("/users/${testUser.id}")
            .andExpect {
                status { isForbidden() } // Spring Security 6.1부터 401이 아닌 403을 기본으로 반환할 수 있음
            }
    }

    @Test
    fun `유효한 토큰으로 보호된 API 접근 성공 테스트`() {
        // 로그인하여 토큰 획득
        val loginRequest = LoginRequest(email = email, password = password)
        val result = mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andReturn()

        val tokenResponse = objectMapper.readTree(result.response.contentAsString)
        val accessToken = tokenResponse.get("accessToken").asText()

        // 토큰을 사용하여 보호된 API 호출
        mockMvc.get("/users/${testUser.id}") {
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            jsonPath("$.email") { value(email) }
        }
    }
}