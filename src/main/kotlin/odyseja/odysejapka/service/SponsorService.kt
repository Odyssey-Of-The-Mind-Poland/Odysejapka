package odyseja.odysejapka.service

import odyseja.odysejapka.domain.Sponsor
import odyseja.odysejapka.rest.SponsorController
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

  fun getImages(): List<List<Sponsor>> {
    val groupedByRow = sponsorRepository.findAll()
      .groupBy { it.rowIndex }
      .toSortedMap()

    return groupedByRow.map { entry ->
      entry.value.sortedBy { it.columnIndex }
        .map { it.toSponsor() }
    }
  }

  fun uploadImage(file: MultipartFile, uploadSponsorRequest: SponsorController.UploadSponsorRequest): Sponsor {
    val type = file.originalFilename?.split(".")?.last()
    if (!acceptedTypes.contains(type)) {
      throw RuntimeException("Provided invalid file type")
    }

    val sponsor = sponsorRepository.save(uploadSponsorRequest.toSponsorEntity(file.bytes))

    changeService.updateVersion()
    return sponsor.toSponsor()
  }

  fun deleteImage(imageId: Int) {
    sponsorRepository.deleteById(imageId)

    changeService.updateVersion()
  }
}