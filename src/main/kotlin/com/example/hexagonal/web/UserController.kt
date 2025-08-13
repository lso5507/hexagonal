package com.example.hexagonal.web

import com.example.hexagonal.domain.port.UserInPort
import com.example.hexagonal.domain.port.dto.UserCreateRequest // Changed import
import com.example.hexagonal.domain.port.dto.UserUpdateRequest // Changed import
import com.example.hexagonal.domain.port.dto.UserResponse // Added import
import org.springframework.http.HttpStatus // Added import
import org.springframework.http.ResponseEntity // Added import
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
    fun getUser(@PathVariable userId: Long): ResponseEntity<UserResponse> { // Added getUser
        val user = userInPort.findUsers(userId) // Changed to findUsers
        return ResponseEntity.ok(user)
    }

    @PutMapping("/{userId}") // Changed PutMapping and added path variable
    fun modifyUser(@PathVariable userId: Long, @RequestBody userUpdateRequest: UserUpdateRequest): ResponseEntity<Void> { // Changed parameter and return type
        userInPort.updateUser(userUpdateRequest.copy(id = userId)) // Changed to updateUser and copy id
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{userId}") // Changed DeleteMapping and added path variable
    fun deleteUser(@PathVariable userId: Long): ResponseEntity<Void> { // Changed parameter and return type
        userInPort.deleteUser(userId)
        return ResponseEntity.noContent().build()
    }
}