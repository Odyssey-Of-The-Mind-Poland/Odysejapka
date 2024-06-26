package odyseja.odysejapka.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/user")
class UserController {

    @GetMapping
    fun checkAuth(principal: Principal?): Authenticated {
        return Authenticated(principal != null)
    }
}

data class Authenticated(val authenticated: Boolean)