package odyseja.odysejapka.form

import org.springframework.stereotype.Service

@Service
class FormAnomalyService {

    fun detectAnomalies(teamForm: TeamForm): List<Anomaly> {
        val anomalies = mutableListOf<Anomaly>()
        anomalies.addAll(detectNoElementAnomalies(teamForm.dtEntries))
        anomalies.addAll(detectPenaltyAnomalies(teamForm.penaltyEntries))
        return anomalies
    }

    private fun detectNoElementAnomalies(entries: List<TeamForm.DtTeamFormEntry>): List<Anomaly> {
        val anomalies = mutableListOf<Anomaly>()
        for (entry in entries) {
            val entryId = entry.entry.id ?: continue
            if (entry.noElement) {
                anomalies.add(
                    Anomaly(
                        entryId = entryId,
                        rule = "no-element-selected",
                        message = "Zaznaczono brak elementu"
                    )
                )
            }
            anomalies.addAll(detectNoElementAnomalies(entry.nestedEntries))
        }
        return anomalies
    }

    private fun detectPenaltyAnomalies(entries: List<TeamForm.PenaltyTeamFormEntry>): List<Anomaly> {
        val anomalies = mutableListOf<Anomaly>()
        for (entry in entries) {
            val entryId = entry.entry.id ?: continue
            val hasPenalty = when (entry.entry.penaltyType) {
                PenaltyFormEntry.PenaltyType.ZERO_BALSA -> entry.zeroBalsa == true
                else -> entry.result != null && entry.result != 0L
            }
            if (hasPenalty) {
                anomalies.add(
                    Anomaly(
                        entryId = entryId,
                        rule = "penalty-points-given",
                        message = "Naliczono punkty karne"
                    )
                )
            }
        }
        return anomalies
    }
}
