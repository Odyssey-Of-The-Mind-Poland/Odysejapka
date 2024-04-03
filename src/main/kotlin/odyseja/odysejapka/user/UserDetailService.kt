package odyseja.odysejapka.user

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsService(
        private var userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val userDetailsEntity: UserDetailsEntity = userRepository.findFirstByUsername(username)
        val authority = ArrayList<GrantedAuthority>()
        authority.add(GrantedAuthority { userDetailsEntity.authority })
        return (User(userDetailsEntity.username, userDetailsEntity.password, authority))
    }
}