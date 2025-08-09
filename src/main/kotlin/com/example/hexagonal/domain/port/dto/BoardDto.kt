package com.example.hexagonal.domain.port.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class BoardDto(
    val id: Long,
    val title: String,
    val content: Int,
){

}
data class createBoardDto(
    @NotBlank
    val title:String,
    @NotBlank
    val content:String,

)
data class ModifyBoardDto(
    @NotNull
    val id: Long,
    @NotBlank
    val title:String,
    @NotBlank
    val content:String,
)