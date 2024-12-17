package day2

import common.readResourceLines
import common.splitRegex
import common.unzipWithNext
import kotlin.math.abs
import kotlin.math.sign

fun main() {
    val reports = readResourceLines("day2/input")
        .map { it.split(splitRegex) }
        .map { it.map(String::toInt) }

    val (safe, unsafe) = reports
        .map { it.zipWithNext() }
        .partition { safeReports(it) }

    val combinations = unsafe
        .map { it.unzipWithNext() }
        .map { it.indices.map { index -> ArrayList(it).apply { removeAt(index) } } }
        .map { it.filter { safeReports(it.zipWithNext()) } }
        .filter { it.isNotEmpty() }

    println(safe.size + combinations.size)
}

private fun safeReports(pairs: List<Pair<Int, Int>>): Boolean =
    pairs.all { (a, b) -> abs(a - b) in 1..3 } &&
            // Make sure the sign of the difference is the same for all pairs
            // This makes sure that the number are in the same order, either ascending or descending
            pairs.map { (a, b) -> (a - b).sign }.toSet().size == 1
