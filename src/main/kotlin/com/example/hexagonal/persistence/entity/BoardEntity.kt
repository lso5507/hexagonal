package com.example.hexagonal.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "board")
class BoardEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var title: String,

    var content: String,

    @ManyToOne(fetch = FetchType.LAZY) // Added ManyToOne relationship
    @JoinColumn(name = "user_id", nullable = false) // Added JoinColumn
    var user: UserEntity, // Made mutable for JPA

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    // No-arg constructor for JPA
    protected constructor() : this(null, "", "", UserEntity(), LocalDateTime.now(), LocalDateTime.now())
}