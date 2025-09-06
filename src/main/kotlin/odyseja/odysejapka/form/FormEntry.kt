package odyseja.odysejapka.form

data class FormEntry(val id: Long?, val name: String, val calcType: CalcType, val category: FormCategory) {

    enum class CalcType {
        SUM, AVERAGE
    }

    enum class FormCategory {
        DT, STYLE, PENALTY
    }
}
