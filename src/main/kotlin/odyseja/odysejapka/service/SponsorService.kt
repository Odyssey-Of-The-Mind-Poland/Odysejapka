package odyseja.odysejapka.service

import odyseja.odysejapka.domain.Sponsor
import odyseja.odysejapka.domain.SponsorEntity
import odyseja.odysejapka.port.SponsorUseCase
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
internal class SponsorService(private val sponsorRepository: SponsorRepository) : SponsorUseCase {

  companion object {
    val acceptedTypes = listOf(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE)
  }

  override fun getImage(id: Int): ByteArray {
    val sponsor = sponsorRepository.findById(id)

    if (sponsor.isEmpty) {
      throw RuntimeException("Provided image id don't exists")
    }

    return sponsor.get().image
  }

  override fun getImages(): List<Sponsor> {
    return sponsorRepository.findAll().map { Sponsor(it.id, it.name) }
  }

  override fun uploadImage(file: MultipartFile, name: String) {
    val type = file.contentType
    if (!acceptedTypes.contains(type)) {
      throw RuntimeException("Provided invalid file type")
    }
    sponsorRepository.save(SponsorEntity(0, name, file.bytes))
  }
}