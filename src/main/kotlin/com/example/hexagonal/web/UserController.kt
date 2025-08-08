package com.example.hexagonal.web

import com.example.hexagonal.domain.port.UserInPort
import com.example.hexagonal.domain.port.dto.ModifyDto
import com.example.hexagonal.domain.port.dto.SignupDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping


@Controller
@RequestMapping("/api/v1/users")
class UserController {
    @Autowired
    lateinit var userInport: UserInPort
    @GetMapping("{id}")
    fun getUsers(@PathVariable id:Long) = userInport.findUsers(id)
    @PostMapping
    fun createUser(@RequestBody signupDto: SignupDto) = userInport.createUser(signupDto)
    @PutMapping
    fun updateUser(@RequestBody modifyDto: ModifyDto) = userInport.updateUser(modifyDto)
    @DeleteMapping("{id}")
    fun deleteUser(@PathVariable id:Long) = userInport.deleteUser(id)

}