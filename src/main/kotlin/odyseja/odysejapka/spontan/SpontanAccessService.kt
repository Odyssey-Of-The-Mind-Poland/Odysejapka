package odyseja.odysejapka.spontan

import odyseja.odysejapka.dashboard.UserAccessService
import odyseja.odysejapka.users.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

/**
 * Centralises all spontan access decisions.
 *
 * - For admins / other non-SPONTAN roles → full access (returns all groups, no verification needed).
 * - For SPONTAN users → only groups where they are the assigned spontan_user.
 */
@Service
class SpontanAccessService(
    private val userAccessService: UserAccessService,
    private val userRepository: UserRepository,
    private val spontanUserRepository: SpontanUserRepository,
    private val spontanGroupAssignmentService: SpontanGroupAssignmentService
) {

    /**
     * Returns the current user's [SpontanUserEntity] if they have the SPONTAN role, null otherwise.
     */
    fun currentSpontanUser(): SpontanUserEntity? {
        if (!userAccessService.isSpontan()) return null
        val principalUserId = extractCurrentPrincipalUserId() ?: return null
        val user = userRepository.findByUserId(principalUserId) ?: return null
        return spontanUserRepository.findByUserId(user.id!!)
    }

    /**
     * Returns only the group assignment IDs that the current user may see for the given city.
     * - Non-SPONTAN users → null (meaning "no filtering, show everything").
     * - SPONTAN users → the set of assignment IDs assigned to them.
     */
    fun accessibleAssignmentIds(cityId: Int): Set<Long>? {
        val spontanUser = currentSpontanUser() ?: return null
        return spontanGroupAssignmentService
            .getAssignmentEntitiesByUser(cityId, spontanUser.id!!)
            .map { it.id }
            .toSet()
    }

    /**
     * Throws 403 if the current user is a SPONTAN user without access to the given group.
     */
    fun verifyGroupAccess(cityId: Int, groupId: GroupId) {
        val spontanUser = currentSpontanUser() ?: return
        val assignment = spontanGroupAssignmentService
            .getAssignmentEntity(cityId, groupId.problem, groupId.age, groupId.league)
        if (assignment.spontanUser?.id != spontanUser.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "No access to this group")
        }
    }

    /**
     * Throws 403 if the current user is a SPONTAN user without access to the group
     * that contains the given performance.
     */
    fun verifyPerformanceAccess(performanceId: Int) {
        val spontanUser = currentSpontanUser() ?: return
        val assignment = spontanGroupAssignmentService.getAssignmentEntityFromPerformance(performanceId)

        if (assignment.spontanUser?.id != spontanUser.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "No access to this group")
        }
    }

    private fun extractCurrentPrincipalUserId(): String? {
        val principal = SecurityContextHolder.getContext().authentication?.principal ?: return null
        return when (principal) {
            is Jwt -> principal.subject
            is UserDetails -> principal.username
            is String -> principal
            else -> null
        }
    }
}
