package com.example.hexagonal.domain.port.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UserDto(
    val id: Long,
    val name: String,
    val age: Int,
){

}
data class SignupDto(
    @NotBlank
    val name:String,
    @Max(100)
    val age:Int
)
data class ModifyDto(
    @NotNull
    val id: Long,
    @NotBlank
    val name:String,
)