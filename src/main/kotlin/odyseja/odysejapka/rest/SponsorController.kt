package odyseja.odysejapka.rest

import odyseja.odysejapka.domain.Sponsor
import odyseja.odysejapka.domain.SponsorEntity
import odyseja.odysejapka.service.SponsorService
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
    fun getImages(): List<List<Sponsor>> {
        return sponsorService.getImages()
    }

    @PostMapping
    fun uploadImage(
        @RequestParam("image") file: MultipartFile,
        @RequestParam("row") row: Int
    ): Sponsor {
        val uploadSponsorRequest = UploadSponsorRequest(row)
        return sponsorService.uploadImage(file, uploadSponsorRequest)
    }

    @DeleteMapping(value = ["/{imageId}"])
    fun deleteImage(@PathVariable imageId: Int) {
        sponsorService.deleteImage(imageId)
    }

    data class UploadSponsorRequest(
        val row: Int
    ) {
        fun toSponsorEntity(image: ByteArray): SponsorEntity {
            return SponsorEntity(0, image, row, 0)
        }
    }
}