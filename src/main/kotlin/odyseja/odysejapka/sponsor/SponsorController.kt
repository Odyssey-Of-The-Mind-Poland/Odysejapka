package odyseja.odysejapka.sponsor

import odyseja.odysejapka.city.CityEntity
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException


@RestController
@RequestMapping("/sponsor")
class SponsorController(private val sponsorService: SponsorService) {

    @GetMapping(
        produces = [MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE],
        value = ["/{imageId}"]
    )
    @ResponseBody
    @Throws(IOException::class)
    fun getImage(@PathVariable imageId: Int): ByteArray? {
        return sponsorService.getImage(imageId)
    }

    @GetMapping
    @ResponseBody
    @Throws(IOException::class)
    fun getImages(@RequestParam("cityId") cityId: Int): List<List<Sponsor>> {
        return sponsorService.getImages(cityId)
    }

    @PostMapping
    fun uploadImage(
        @RequestParam("image") file: MultipartFile,
        @RequestParam("row") row: Int,
        @RequestParam("cityId") cityId: Int
    ): Sponsor {
        val uploadSponsorRequest = UploadSponsorRequest(row)
        return sponsorService.uploadImage(file, cityId, uploadSponsorRequest)
    }

    @DeleteMapping(value = ["/{imageId}"])
    fun deleteImage(@PathVariable imageId: Int) {
        sponsorService.deleteImage(imageId)
    }

    data class UploadSponsorRequest(
        val row: Int
    ) {
        fun toSponsorEntity(image: ByteArray, cityEntity: CityEntity): SponsorEntity {
            return SponsorEntity(0, image, row, 0, cityEntity)
        }
    }
}