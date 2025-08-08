package com.example.hexagonal.persistence.repository

import com.example.hexagonal.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<UserEntity,Long> {
}