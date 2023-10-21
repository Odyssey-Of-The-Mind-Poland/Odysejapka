package odyseja.odysejapka.planner

import odyseja.odysejapka.service.StageRepository
import org.optaplanner.core.api.solver.SolverFactory
import org.optaplanner.core.config.solver.SolverConfig
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Service
class PlannerService(private val teamRepository: TeamRepository, private val stageRepository: StageRepository) {

    fun plan(): TimeTable? {
        val solverFactory = SolverFactory.create<TimeTable>(
            SolverConfig()
                .withSolutionClass(TimeTable::class.java)
                .withEntityClasses(Performance::class.java)
                .withConstraintProviderClass(TimeTableConstraintProvider::class.java)
                .withTerminationSpentLimit(Duration.ofSeconds(5))
        )

        val problem: TimeTable = generateDemoData()

        val solver = solverFactory.buildSolver()
        return solver.solve(problem)
    }

    private fun generateDemoData(): TimeTable {
        val timeSlot = generateTimeSlots()
        val stages = stageRepository.findAllByCityEntity_Id(0).map { it.toStage() }
        val teams = teamRepository.findAll().map { it.toPerformance() }
        return TimeTable(timeSlot, stages, teams)
    }

    fun generateTimeSlots(): List<TimeSlot> {
        val slots = mutableListOf<TimeSlot>()
        var startTime = LocalTime.of(8, 30)
        val endTime = LocalTime.of(16, 30)

        while (startTime.isBefore(endTime)) {
            val slotEnd = startTime.plus(20, ChronoUnit.MINUTES)
            slots.add(TimeSlot(startTime, slotEnd))
            startTime = slotEnd
        }

        return slots
    }
}