package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceLines
import common.splitRegex
import common.unzipWithNext
import kotlin.math.abs
import kotlin.math.sign

@AutoService(AOCSolution::class)
class Day02 : AOCSolution {
    override val year = 2024
    override val day = 2

    override fun part1(inputFile: String): String {
        val reports = readResourceLines(inputFile)
            .map { string -> string.split(splitRegex) }
            .map { it.map(String::toInt) }

        val result = reports
            .map { it.zipWithNext() }
            .count { pairs -> safeReports(pairs) }

        return result.toString()
    }

    override fun part2(inputFile: String): String {
        val reports = readResourceLines(inputFile)
            .map { it.split(splitRegex) }
            .map { it.map(String::toInt) }

        val (safe, unsafe) = reports
            .map { it.zipWithNext() }
            .partition { safeReports(it) }

        val combinations = unsafe
            .map { it.unzipWithNext() }
            // Remove one element at a time and check if the remaining elements are safe
            .map { it.indices.map { index -> ArrayList(it).apply { removeAt(index) } } }
            .map { it.filter { report -> safeReports(report.zipWithNext()) } }
            // Remove empty lists where we have no safe combinations
            .filter { it.isNotEmpty() }

        return (safe.size + combinations.size).toString()
    }

    private fun safeReports(pairs: List<Pair<Int, Int>>): Boolean =
        pairs.all { (a, b) -> abs(a - b) in 1..3 } &&
                // Make sure the sign of the difference is the same for all pairs
                // This makes sure that the number are in the same order, either ascending or descending
                pairs.map { (a, b) -> (a - b).sign }.toSet().size == 1
}