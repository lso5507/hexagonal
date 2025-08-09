package com.example.hexagonal.domain

class Board(
    val id:Long?=null,
    val title : String,
    val content : String,
) {
    fun checkOwnerBoard(sessionUserId: Long,requestUserId: Long) = sessionUserId == requestUserId

}