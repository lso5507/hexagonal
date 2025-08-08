package com.example.hexagonal.domain

class User(
    var id:Long?=null,
    val name:String,
    val age:Int,
) {
    /* 도메인 비즈니스 규칙은 여기서 */
    fun validatePassword(source: String,target:String) = source == target
}