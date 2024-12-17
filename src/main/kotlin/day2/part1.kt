package day2

import common.readResourceLines
import common.splitRegex
import kotlin.math.abs
import kotlin.math.sign

fun main() {
    val reports = readResourceLines("day2/input")
        .map { string -> string.split(splitRegex) }
        .map { it.map(String::toInt) }

    val result = reports
        .map { it.zipWithNext() }
        .count {
            it.all { (a, b) -> abs(a - b) in 1..3 } &&
                    // Make sure the sign of the difference is the same for all pairs
                    // This makes sure that the number are in the same order, either ascending or descending
                    it.map { (a, b) -> (a - b).sign }.toSet().size == 1
        }

    println(result)
}
