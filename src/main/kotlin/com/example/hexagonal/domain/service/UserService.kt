package com.example.hexagonal.domain.service

import com.example.hexagonal.domain.port.UserAssembler
import com.example.hexagonal.domain.port.UserInPort
import com.example.hexagonal.domain.port.UserOutport
import com.example.hexagonal.domain.port.dto.ModifyDto
import com.example.hexagonal.domain.port.dto.SignupDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService : UserInPort {
    @Autowired
    lateinit var userOutPort: UserOutport
    override fun findUsers(id: Long) = userOutPort.findUsers(id).let{ UserAssembler.from(it) }

    override fun createUser(signupDto: SignupDto) = userOutPort.saveUser(UserAssembler.toCreate(signupDto))

    override fun updateUser(modifyDto: ModifyDto) = userOutPort.updateUser(UserAssembler.toUpdate(modifyDto))

    override fun deleteUser(id: Long) = userOutPort.deleteUser(id)

}