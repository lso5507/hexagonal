package com.example.hexagonal.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime // Added import

@Entity
@Table(name = "TB_USER")
class UserEntity(
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Changed to IDENTITY for auto-increment
    val id: Long? = null, // Made nullable

    @Column(length = 50) // Increased length
    var name: String = "",
    @Column(length = 255) // Explicitly set length for email
    var email: String? = "",

    @Column(length = 255)
    var password: String = "",

    @Column
    var roles: String = "USER",

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    // No-arg constructor for JPA
    protected constructor() : this(null, "", "", "", "USER", LocalDateTime.now(), LocalDateTime.now())
}