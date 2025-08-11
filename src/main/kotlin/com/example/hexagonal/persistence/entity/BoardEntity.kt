package com.example.hexagonal.persistence.entity

import jakarta.persistence.*

@Entity
class BoardEntity(
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    val id: Long = 0L,
) {
    init{
        require(id >= 0) {"id must be greater than or equal to zero"}
    }
    @Column(nullable = false, length = 20)
    lateinit var title: String
    @Column(nullable = false, length = 1000)
    lateinit var content: String
    @ManyToOne
    @JoinColumn(name = "user_id")
    lateinit var user: UserEntity
}