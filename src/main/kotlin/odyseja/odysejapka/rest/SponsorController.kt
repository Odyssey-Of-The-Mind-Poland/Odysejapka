package odyseja.odysejapka.rest

import odyseja.odysejapka.port.SponsorUseCase
import org.springframework.http.MediaType
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException


@RestController
@RequestMapping("/sponsor")
class SponsorController(private val sponsorUseCase: SponsorUseCase) {

  @GetMapping(
    produces = [MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE],
    value = ["/{imageId}"]
  )
  @ResponseBody
  @Throws(IOException::class)
  fun getImage(@PathVariable imageId: Int): ByteArray? {
    return sponsorUseCase.getImage(imageId)
  }

  @GetMapping
  @ResponseBody
  @Throws(IOException::class)
  fun getImages(): List<Int> {
    return sponsorUseCase.getImages()
  }

  @PostMapping
  @Secured("ROLE_ADMIN")
  fun uploadImage(
    @RequestParam("image") file: MultipartFile
  ): String? {
    sponsorUseCase.uploadImage(file)
    return "File uploaded successfully"
  }
}