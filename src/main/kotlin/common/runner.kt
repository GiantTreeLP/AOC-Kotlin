package common

import java.util.*
import kotlin.time.measureTimedValue

class Runner {
    private val solutions = mutableListOf<AOCSolution>()

    fun discoverSolutions() {
        val loader = ServiceLoader.load(AOCSolution::class.java)
        solutions.addAll(loader)
        solutions.sortedWith(
            Comparator.comparingInt(AOCSolution::year)
                .thenComparingInt(AOCSolution::day)
        )
    }

    fun run() {
        println("Available solutions:")
        solutions.forEach {
            println(" - %s (%04d/%02d)".format(it::class.simpleName, it.year, it.day))
        }
        println()

        solutions.forEach { solution ->
            println("Running solutions for %04d/%02d...".format(solution.year, solution.day))
            val sampleInputFile = "day%02d/sample".format(solution.day)
            val actualInputFile = "day%02d/input".format(solution.day)

            if (!isResourceAvailable(sampleInputFile)) {
                error("Sample input file not found: $sampleInputFile")
            }

            if (!isResourceAvailable(actualInputFile)) {
                error("Actual input file not found: $actualInputFile")
            }

            val part1 = measureTimedValue { solution.part1(sampleInputFile) }
            val part2 = measureTimedValue { solution.part2(sampleInputFile) }

            println("Part 1: ${part1.value} (${part1.duration})")
            println("Part 2: ${part2.value} (${part2.duration})")
        }
    }

}

fun main() {
    val runner = Runner()
    runner.discoverSolutions()

    runner.run()
}