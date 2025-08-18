package com.example.hexagonal.infrastructure.security

import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority

class JwtTokenProviderTest {

    private lateinit var jwtTokenProvider: JwtTokenProvider

    @BeforeEach
    fun setUp() {
        val secret = "V29vTmVvQ29kaW5nSGV4YWdvbmFsQXJjaGl0ZWN0dXJlUHJvamVjdFR1dG9yaWFsU3ByaW5nQm9vdA=="
        val expiration: Long = 3600
        jwtTokenProvider = JwtTokenProvider(secret, expiration)
    }

    @Test
    fun `토큰 생성 및 검증 테스트`() {
        // given
        val email = "test@test.com"
        val roles = listOf("USER")

        // when
        val tokenResponse = jwtTokenProvider.generateToken(email, roles)
        val accessToken = tokenResponse.accessToken

        // then
        assertThat(accessToken).isNotNull()
        assertThat(jwtTokenProvider.validateToken(accessToken)).isTrue()
    }

    @Test
    fun `토큰에서 인증 정보 추출 테스트`() {
        // given
        val email = "admin@test.com"
        val roles = listOf("USER", "ADMIN")
        val token = jwtTokenProvider.generateToken(email, roles).accessToken

        // when
        val extractedAuth = jwtTokenProvider.getAuthentication(token)

        // then
        assertThat(extractedAuth.name).isEqualTo(email)
        assertThat(extractedAuth.authorities.map { it.authority }).containsAll(roles)
    }

    @Test
    fun `유효하지 않은 토큰 검증 테스트`() {
        // given
        val invalidToken = "invalid-token"

        // when
        val isValid = jwtTokenProvider.validateToken(invalidToken)

        // then
        assertThat(isValid).isFalse()
    }
}
