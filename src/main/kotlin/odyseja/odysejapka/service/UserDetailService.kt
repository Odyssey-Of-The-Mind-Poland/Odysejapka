package odyseja.odysejapka.service

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
        val userDetails: odyseja.odysejapka.domain.UserDetails = userRepository.findFirstByUsername(username)
        val authority = ArrayList<GrantedAuthority>()
        authority.add(GrantedAuthority { userDetails.authority })
        return (User(userDetails.username, userDetails.password, authority))
    }
}