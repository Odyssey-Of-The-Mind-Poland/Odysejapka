package odyseja.odysejapka.planner

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore
import org.optaplanner.core.api.score.stream.Constraint
import org.optaplanner.core.api.score.stream.ConstraintFactory
import org.optaplanner.core.api.score.stream.ConstraintProvider
import org.optaplanner.core.api.score.stream.Joiners

class TimeTableConstraintProvider : ConstraintProvider {
    override fun defineConstraints(constraintFactory: ConstraintFactory?): Array<Constraint> {
        return arrayOf(
            stageConflict(constraintFactory!!),
            teamConflict(constraintFactory)
        )
    }

    fun stageConflict(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory
            .forEachUniquePair(
                Performance::class.java,
                Joiners.equal(Performance::stage),
                Joiners.equal(Performance::stage)
            )
            .penalize(HardSoftScore.ONE_HARD)
            .asConstraint("Room conflict")
    }

    fun teamConflict(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory
            .forEachUniquePair(
                Performance::class.java,
                Joiners.equal(Performance::teamName),
                Joiners.equal(Performance::teamName)
            )
            .penalize(HardSoftScore.ONE_HARD)
            .asConstraint("Team conflict")
    }
}