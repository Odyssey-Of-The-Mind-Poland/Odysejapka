package odyseja.odysejapka.form

data class FormEntry(val id: Long?, val name: String, val calcType: CalcType) {

    enum class CalcType {
        SUM, AVERAGE
    }
}
