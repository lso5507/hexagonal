package com.example.hexagonal.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "TB_USER")
class UserEntity(
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = 0L,

) {
    init{
        require(id >= 0) {"id must be greater than or equal to zero"}
    }
    @Column(length = 10)
    var name: String? = ""
    @Column
    var age: Int? = -1
}