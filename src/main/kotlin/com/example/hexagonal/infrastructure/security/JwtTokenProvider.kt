package com.example.hexagonal.infrastructure.security

import com.example.hexagonal.domain.port.TokenProviderPort
import com.example.hexagonal.domain.port.dto.TokenResponse
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("${"$"}{jwt.secret}") private val secretKey: String,
    @Value("${"$"}{jwt.access-token-validity-in-seconds}") private val accessTokenValidityInSeconds: Long
) : TokenProviderPort {
    private val key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    override fun generateToken(email: String, roles: List<String>): TokenResponse {
        val authorities: String = roles.joinToString(",")
        val now = Date()
        val accessTokenExpiresIn = Date(now.time + accessTokenValidityInSeconds * 1000)

        val accessToken = Jwts.builder()
            .setSubject(email)
            .claim("auth", authorities)
            .setIssuedAt(now)
            .setExpiration(accessTokenExpiresIn)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

        return TokenResponse(accessToken = accessToken)
    }

    fun getAuthentication(accessToken: String): Authentication {
        val claims: Claims = parseClaims(accessToken)

        val authorities: Collection<GrantedAuthority> =
            claims["auth"]?.toString()?.split(",")?.map { SimpleGrantedAuthority(it) } ?: emptyList()

        val principal: UserDetails = User(claims.subject, "", authorities)
        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            return true
        } catch (e: SecurityException) {
            // log
        } catch (e: MalformedJwtException) {
            // log
        } catch (e: ExpiredJwtException) {
            // log
        } catch (e: UnsupportedJwtException) {
            // log
        } catch (e: IllegalArgumentException) {
            // log
        }
        return false
    }

    private fun parseClaims(accessToken: String): Claims {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).body
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }
}
