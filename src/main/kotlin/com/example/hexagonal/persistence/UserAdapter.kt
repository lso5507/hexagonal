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
    override fun findUsers(id: Long): User =userRepository.findById(id).orElseThrow(
        {NoSuchElementException("User with id $id not found")}
    ).toDomain


    override fun saveUser(user: User): User {
        val userEntity = user.toEntity()
        val savedEntity = userRepository.save(userEntity)
        val domainUser = savedEntity.toDomain
        return domainUser
    }

    override fun deleteUser(id: Long)  {
        userRepository.deleteById(id)
    }

    override fun updateUser(user: User) {
        userRepository.findById(user.id!!).get().let {
            it.name = user.name
            it.email = user.email
        }
    }
}