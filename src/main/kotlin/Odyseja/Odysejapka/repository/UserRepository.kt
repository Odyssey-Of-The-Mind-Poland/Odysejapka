package Odyseja.Odysejapka.repository

import Odyseja.Odysejapka.data.model.UserDetails
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserDetails, Int> {
    fun findFirstByUsername(username: String) : UserDetails?
}