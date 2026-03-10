package odyseja.odysejapka.form

enum class FormState(val label: String) {
    NOT_SCORED("Nieocenione"),
    ERROR("Błędy w formularzu"),
    ANOMALY("Anomalia"),
    APPROVED("Zatwierdzone"),
    SCORED("Ocenione");
}
