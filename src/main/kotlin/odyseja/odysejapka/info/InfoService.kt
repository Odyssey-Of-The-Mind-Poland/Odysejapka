package odyseja.odysejapka.info

import odyseja.odysejapka.change.ChangeService
import odyseja.odysejapka.city.CityService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InfoService(
    private val infoRepository: InfoRepository,
    private val cityService: CityService,
    private val infoCategoryRepository: InfoCategoryRepository,
    private val changeService: ChangeService
) {

    fun getInfo(cityId: Int?): Iterable<Info?>? {
        val city = if (cityId == null) cityService.getFinals()else cityService.getCity(cityId)
        return (infoRepository.findByCity(city)
        ).map { it?.toInfo() }
            .sortedByDescending { it?.sortNumber }
    }

    fun getInfoCategory(): MutableIterable<InfoCategoryEntity> {
        return infoCategoryRepository.findAll()
    }

    @Transactional
    fun addInfo(info: Info): Info {
        val savedInfo = infoRepository.save(
            InfoEntity(
                info.id,
                info.infoName,
                info.infoText,
                cityService.getCity(info.city),
                infoCategoryRepository.findFirstById(info.category)
                    ?: addInfoCategory(InfoCategoryEntity(0, info.categoryName)),
                info.sortNumber,
                info.icon,
                info.color
            )
        )
        changeService.updateVersion()
        return savedInfo.toInfo()
    }

    @Transactional
    fun addInfoCategory(infoCategory: InfoCategoryEntity): InfoCategoryEntity {
        val savedInfoCategory = infoCategoryRepository.save(infoCategory)
        changeService.updateVersion()
        return savedInfoCategory
    }

    @Transactional
    fun updateInfo(info: Info): Info {
        val infoEntity = infoRepository.findById(info.id).get()
        infoEntity.infoText = info.infoText
        infoEntity.sortNumber = info.sortNumber
        infoEntity.icon = info.icon
        infoEntity.color = info.color
        infoRepository.save(infoEntity)

        changeService.updateVersion()
        return info
    }

    @Transactional
    fun deleteInfo(id: Int) {
        infoRepository.deleteById(id)
        changeService.updateVersion()
    }

    @Transactional
    fun deleteInfoByCity(cityId: Int) {
        infoRepository.deleteByCityId(cityId)
        changeService.updateVersion()
    }

    @Transactional
    fun deleteInfoCategory(categoryId: Int) {
        infoRepository.deleteByCategoryId(categoryId)
        infoCategoryRepository.deleteById(categoryId)
        changeService.updateVersion()
    }

    fun getInfoById(info: Int): Info {
        return infoRepository.findById(info).get().toInfo()
    }
}