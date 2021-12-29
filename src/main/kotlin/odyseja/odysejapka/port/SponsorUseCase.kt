package odyseja.odysejapka.port

import odyseja.odysejapka.domain.Sponsor
import org.springframework.web.multipart.MultipartFile

interface SponsorUseCase {

  fun getImage(id: Int): ByteArray

  fun getImages(): List<Sponsor>

  fun uploadImage(file: MultipartFile, name: String)

  fun deleteImage(imageId: Int)
}