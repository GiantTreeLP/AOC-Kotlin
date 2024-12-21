package common

import java.util.*
import kotlin.time.measureTimedValue

class Runner(private val years: List<Int>, private val days: List<Int>) {
    constructor() : this(emptyList(), emptyList())
    constructor(years: List<Int>) : this(years, emptyList())

    private val solutions = mutableListOf<AOCSolution>()

    fun discoverSolutions() {
        val loader = ServiceLoader.load(AOCSolution::class.java).toMutableList()
        if (years.isNotEmpty()) {
            loader.removeIf { it.year !in years }
        }
        if (days.isNotEmpty()) {
            loader.removeIf { it.day !in days }
        }
        solutions.addAll(loader)
        solutions.sortedWith(
            Comparator.comparingInt(AOCSolution::year)
                .thenComparingInt(AOCSolution::day)
        )
    }

    fun run() {
        println("Available solutions (${solutions.size}):")
        solutions.forEach {
            println(" - %s (%04d/%02d)".format(it::class.simpleName, it.year, it.day))
        }
        println()

        solutions.forEach { solution ->
            println("Running solutions for %04d/%02d...".format(solution.year, solution.day))
            val sampleInputFile = "year%04d/day%02d/sample".format(solution.year, solution.day)
            val actualInputFile = "year%04d/day%02d/input".format(solution.year, solution.day)

            if (!isResourceAvailable(sampleInputFile)) {
                error("Sample input file not found: $sampleInputFile")
            }

            if (!isResourceAvailable(actualInputFile)) {
                error("Actual input file not found: $actualInputFile")
            }

            println("Results and times for part 1:")
            val part1Sample = measureTimedValue { solution.part1(sampleInputFile) }
            println("Part 1 sample input: ${part1Sample.value} (${part1Sample.duration})")
            val part1Actual = measureTimedValue { solution.part1(actualInputFile) }
            println("Part 1 actual input: ${part1Actual.value} (${part1Actual.duration})")
            println()


            println("Results and times for part 2:")
            val part2Sample = measureTimedValue { solution.part2(sampleInputFile) }
            println("Part 2 sample input: ${part2Sample.value} (${part2Sample.duration})")
            val part2Actual = measureTimedValue { solution.part2(actualInputFile) }
            println("Part 2 actual input: ${part2Actual.value} (${part2Actual.duration})")
            println()
        }
    }

}

fun main() {
    val runner = Runner(listOf(2024))
    runner.discoverSolutions()

    runner.run()
}