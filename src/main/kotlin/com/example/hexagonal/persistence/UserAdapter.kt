package com.example.hexagonal.persistence

import com.example.hexagonal.domain.User
import com.example.hexagonal.domain.port.UserOutPort
import com.example.hexagonal.persistence.entity.toDomain
import com.example.hexagonal.persistence.entity.toEntity
import com.example.hexagonal.persistence.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class UserAdapter: UserOutPort {
    @Autowired
    lateinit var userRepository: UserRepository
    override fun findUsers(id: Long): User =userRepository.findById(id).isPresent.let {
        userRepository.findById(id).get().toDomain
    }


    override fun saveUser(user: User): Boolean = userRepository.save(user.toEntity(1L)) != null

    override fun deleteUser(id: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun updateUser(user: User): Boolean {
        TODO("Not yet implemented")
    }
}