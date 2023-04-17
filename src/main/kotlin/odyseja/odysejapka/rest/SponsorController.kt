package odyseja.odysejapka.rest

import odyseja.odysejapka.domain.Sponsor
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
  fun getImages(): List<Sponsor> {
    return sponsorService.getImages()
  }

  @PostMapping
  fun uploadImage(
    @RequestParam("image") file: MultipartFile, @RequestParam("name") name: String
  ): String? {
    sponsorService.uploadImage(file, name)
    return "File uploaded successfully"
  }

  @DeleteMapping(value = ["/{imageId}"])
  fun deleteImage(@PathVariable imageId: Int) {
    sponsorService.deleteImage(imageId)
  }
}