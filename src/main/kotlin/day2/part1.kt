package day2

import common.getResourceAsStream
import common.splitRegex
import kotlin.math.abs
import kotlin.math.sign

fun main() {
    val reports = getResourceAsStream("day2/input")
        .bufferedReader()
        .readLines()
        .map { string -> string.split(splitRegex) }
        .map { it.map { it.toInt() } }

    val result = reports
        .map { it.zipWithNext() }
        .count { it.all { (a, b) -> abs(a - b) in 1..3 } && it.map { (a, b) -> (a - b).sign }.toSet().size == 1 }

    println(result)
}
