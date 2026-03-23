package odyseja.odysejapka.sponsor

import jakarta.persistence.EntityNotFoundException
import odyseja.odysejapka.change.ChangeService
import odyseja.odysejapka.city.CityEntity
import odyseja.odysejapka.city.CityService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class SponsorService(
    private val sponsorRepository: SponsorRepository,
    private val changeService: ChangeService,
    private val cityService: CityService
) {

    companion object {
        val acceptedTypes = listOf("jpg", "jpeg", "png")
    }

    fun getSponsor(sponsorId: Int): SponsorEntity {
        return sponsorRepository.findFirstById(sponsorId)
            ?: throw EntityNotFoundException("Nie znaleziono sponsora o ID $sponsorId")
    }

    fun getImage(sponsorId: Int): ByteArray {
        val sponsor = getSponsor(sponsorId)
        return sponsor.image
    }

    @Transactional
    fun getImages(cityId: Int): List<List<Sponsor>> {
        val groupedByRow = sponsorRepository.findAllByCityId(cityId)
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

        val city = cityService.getCity(cityId)
        val sponsor = sponsorRepository.save(toSponsorEntity(file.bytes, city, uploadSponsorRequest.row))

        changeService.updateVersion()
        return sponsor.toSponsor()
    }

    @Transactional
    fun deleteSponsor(sponsorId: Int) {
        sponsorRepository.deleteById(sponsorId)
        changeService.updateVersion()
    }

    @Transactional
    fun deleteSponsorsByCity(cityId: Int) {
        sponsorRepository.deleteByCityId(cityId)
        changeService.updateVersion()
    }

    private fun toSponsorEntity(image: ByteArray, cityEntity: CityEntity, row: Int): SponsorEntity {
        val lastColumn = sponsorRepository.findMaxColumnOrderByRowOrderAndCityId(row, cityEntity.id) ?: -1
        return SponsorEntity(0, image, row, lastColumn + 1, cityEntity)
    }
}