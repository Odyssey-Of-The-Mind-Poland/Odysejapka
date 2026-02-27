package odyseja.odysejapka.form

enum class FormState(val label: String) {
    NOT_SCORED("Nie ocenione"),
    ERROR("Błędy w formularzu"),
    ANOMALY("Anomalia"),
    APPROVED("Zatwierdzony"),
    SCORED("Ocenione");
}
