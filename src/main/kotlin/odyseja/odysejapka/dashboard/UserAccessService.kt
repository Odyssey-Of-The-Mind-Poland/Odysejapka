package odyseja.odysejapka.dashboard

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class UserAccessService {

    fun isAdmin(): Boolean = hasAuthority("ROLE_ADMINISTRATOR")

    fun isKapitan(): Boolean = hasAuthority("ROLE_KAPITAN")

    fun isSpontan(): Boolean = hasAuthority("ROLE_SPONTAN")

    fun isKapitanForProblem(problem: Int): Boolean = isKapitan() && hasProblemRole(problem)

    fun hasProblemRole(problem: Int): Boolean = hasAuthority("ROLE_PROBLEM_$problem")

    private fun hasAuthority(authority: String): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication ?: return false
        return authentication.authorities.any { it.authority == authority }
    }
}
