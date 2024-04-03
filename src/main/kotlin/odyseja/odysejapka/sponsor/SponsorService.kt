package odyseja.odysejapka.sponsor

import odyseja.odysejapka.change.ChangeService
import odyseja.odysejapka.city.CityRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class SponsorService(
  private val sponsorRepository: SponsorRepository,
  private val changeService: ChangeService,
  private val cityRepository: CityRepository
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

  @Transactional
  fun getImages(cityId: Int): List<List<Sponsor>> {
    val groupedByRow = sponsorRepository.findAllByCity_Id(cityId)
      .groupBy { it.rowOrder }
      .toSortedMap()

    return groupedByRow.map { entry ->
      entry.value.sortedBy { it.columnOrder }
        .map { it.toSponsor() }
    }
  }

  @Transactional
  fun uploadImage(
    file: MultipartFile,
    cityId: Int,
    uploadSponsorRequest: SponsorController.UploadSponsorRequest
  ): Sponsor {
    val type = file.originalFilename?.split(".")?.last()
    if (!acceptedTypes.contains(type)) {
      throw RuntimeException("Provided invalid file type")
    }

    val city = cityRepository.findFirstById(cityId)
    val sponsor = sponsorRepository.save(uploadSponsorRequest.toSponsorEntity(file.bytes, city))

    changeService.updateVersion()
    return sponsor.toSponsor()
  }

  fun deleteImage(imageId: Int) {
    sponsorRepository.deleteById(imageId)

    changeService.updateVersion()
  }
}