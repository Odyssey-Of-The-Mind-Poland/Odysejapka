package odyseja.odysejapka.cache

import jakarta.persistence.*

@Entity
@Table(name = "cache_entry")
class CacheEntity(
    @Id
    @Column(name = "cache_key", length = 255)
    val key: String,

    @Column(columnDefinition = "TEXT", nullable = false)
    val value: String
)
