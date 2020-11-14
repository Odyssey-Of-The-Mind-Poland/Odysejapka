package Odyseja.Odysejapka.Resources

import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/user")
class UserController(){

    @Secured("ROLE_USER")
    @GetMapping
    fun CheckAuth(): Authenticated{
        return Authenticated(true)
    }
}

data class Authenticated(val authenticated: Boolean)