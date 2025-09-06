package odyseja.odysejapka.form

data class FormEntry(val name: String, val calcType: CalcType) {

    enum class CalcType {
        SUM, AVERAGE
    }
}
