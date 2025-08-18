package com.example.hexagonal.domain.port.dto

import com.example.hexagonal.domain.Board // Added import for Board
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
// Removed unused import jakarta.validation.constraints.NotEmpty

data class BoardDto(
    val id: Long,
    val title: String,
    val content: String,
    val userId: Long // Added userId
){
    companion object { // Added companion object for fromDomain
        fun fromDomain(board: Board): BoardDto {
            return BoardDto(
                id = board.id!!,
                title = board.title,
                content = board.content,
                userId = board.userId
            )
        }
    }
}
data class CreateBoardDto(
    @field:NotNull // Added @field:NotNull
    @field:NotBlank // Added @field:NotBlank
    val title:String,
    @field:NotNull // Added @field:NotNull
    @field:NotBlank // Added @field:NotBlank
    val content:String,
    @field:NotNull // Added @field:NotNull for userId
    val userId: Long // Added userId
) {
    fun toDomain(): Board { // Added toDomain method
        return Board(
            title = this.title,
            content = this.content,
            userId = this.userId
        )
    }
}
data class ModifyBoardDto(
    @field:NotNull // Added @field:NotNull
    val id: Long,
    @field:NotNull // Added @field:NotNull
    @field:NotBlank // Added @field:NotBlank
    val title:String,
    @field:NotNull // Added @field:NotNull
    @field:NotBlank // Added @field:NotBlank
    val content:String
) {
    fun toDomain(): Board { // Added toDomain method
        return Board(
            id = this.id,
            title = this.title,
            content = this.content,
            userId = 0 // Will be ignored, but required by Board constructor
        )
    }
}