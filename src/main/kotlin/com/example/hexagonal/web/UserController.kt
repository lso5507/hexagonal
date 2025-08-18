package com.example.hexagonal.web

import com.example.hexagonal.domain.port.UserInPort
import com.example.hexagonal.domain.port.dto.UserCreateRequest // Changed import
import com.example.hexagonal.domain.port.dto.UserUpdateRequest // Changed import
import com.example.hexagonal.domain.port.dto.UserResponse // Added import
import org.springframework.http.HttpStatus // Added import
import org.springframework.http.ResponseEntity // Added import
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController // Changed to RestController
@RequestMapping("/users") // Changed mapping
class UserController(
    private val userInPort: UserInPort // Changed to constructor injection
) {
    @PostMapping
    fun createUser(@RequestBody userCreateRequest: UserCreateRequest): ResponseEntity<UserResponse> { // Changed parameter and return type
        val createdUser = userInPort.createUser(userCreateRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser)
    }

    @GetMapping("/{userId}") // Added GetMapping
    fun getUser(@PathVariable userId: Long, @AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<UserResponse> { // Added getUser
        // userDetails.username 으로 현재 인증된 사용자의 이메일(username)에 접근할 수 있습니다.
        // 예를 들어, 요청한 userId가 본인의 ID인지 검증하는 로직을 추가할 수 있습니다.
        // if (userInPort.findUsers(userId).email != userDetails.username) { throw AccessDeniedException(...) }
        val user = userInPort.findUsers(userId) // Changed to findUsers
        return ResponseEntity.ok(user)
    }

    @PutMapping("/{userId}") // Changed PutMapping and added path variable
    fun modifyUser(@PathVariable userId: Long, @RequestBody userUpdateRequest: UserUpdateRequest): ResponseEntity<Void> { // Changed parameter and return type
        userInPort.updateUser(userUpdateRequest.copy(id = userId)) // Changed to updateUser and copy id
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{userId}") // Changed DeleteMapping and added path variable
    fun deleteUser(@PathVariable userId: Long, @AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<Void> { // Changed parameter and return type
        userInPort.deleteUser(userId, userDetails.username)
        return ResponseEntity.noContent().build()
    }
}