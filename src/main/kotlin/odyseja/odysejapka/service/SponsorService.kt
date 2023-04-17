package odyseja.odysejapka.service

import odyseja.odysejapka.domain.Sponsor
import odyseja.odysejapka.domain.SponsorEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class SponsorService(
  private val sponsorRepository: SponsorRepository,
  private val changeService: ChangeService
) {

  companion object {
    val acceptedTypes = listOf("jpg", "jpeg", "png")
  }

  fun getImage(id: Int): ByteArray {
    val sponsor = sponsorRepository.findById(id)

    if (sponsor.isEmpty) {
      throw RuntimeException("Provided image id don't exists")
    }

    return sponsor.get().image
  }

  fun getImages(): List<Sponsor> {
    return sponsorRepository.findAll().map { Sponsor(it.id, it.name) }
  }

  fun uploadImage(file: MultipartFile, name: String) {
    val type = file.originalFilename?.split(".")?.last()
    if (!acceptedTypes.contains(type)) {
      throw RuntimeException("Provided invalid file type")
    }
    sponsorRepository.save(SponsorEntity(0, name, file.bytes))

    changeService.updateVersion()
  }

  fun deleteImage(imageId: Int) {
    sponsorRepository.deleteById(imageId)

    changeService.updateVersion()
  }
}