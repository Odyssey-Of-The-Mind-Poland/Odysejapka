package odyseja.odysejapka.service

import odyseja.odysejapka.domain.SponsorEntity
import org.springframework.data.repository.CrudRepository

interface SponsorRepository : CrudRepository<SponsorEntity, Int> {
}