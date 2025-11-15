package odyseja.odysejapka.roles

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/roles")
class RolesRestController {

    @GetMapping
    fun getAllowedRoles(): List<Role> {
        return Role.entries
    }
}