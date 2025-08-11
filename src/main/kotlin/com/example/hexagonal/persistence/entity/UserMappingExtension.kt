package com.example.hexagonal.persistence.entity

import com.example.hexagonal.domain.User

val UserEntity.toDomain: User
    get() = User(
        id = this.id,
        name = this.name?:"",
        age = this.age?:0
    )
fun User.toEntity(): UserEntity=
    UserEntity().apply{
        name=this@toEntity.name
        age=this@toEntity.age
    }