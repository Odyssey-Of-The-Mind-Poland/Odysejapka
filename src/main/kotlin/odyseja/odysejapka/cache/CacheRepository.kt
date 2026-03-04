package odyseja.odysejapka.cache

import org.springframework.data.jpa.repository.JpaRepository

interface CacheRepository : JpaRepository<CacheEntity, String>
