package odyseja.odysejapka.port

import org.springframework.web.multipart.MultipartFile

interface SponsorUseCase {

  fun getImage(id: Int): ByteArray

  fun getImages(): List<Int>

  fun uploadImage(file: MultipartFile)
}