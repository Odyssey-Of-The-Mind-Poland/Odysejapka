package odyseja.odysejapka.qr

import java.sql.Timestamp
import jakarta.persistence.*

@Entity(name = "scan")
class ScanEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    val id: Int,
    @Column
    val device: String,
    @Column
    val scannedAt: Timestamp
)