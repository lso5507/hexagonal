package com.example.hexagonal.persistence.repository

import com.example.hexagonal.persistence.entity.BoardEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BoardRepository : JpaRepository<BoardEntity, Long> {
}