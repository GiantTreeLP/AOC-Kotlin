package day1

import common.getResourceAsStream
import common.pairs
import common.splitRegex
import common.transpose
import kotlin.math.abs

fun main() {
    val input = getResourceAsStream("day1/input")
        .bufferedReader()
        .readLines()
        .map { it.split(splitRegex) }
        .map { it.map { it.toInt() } }
        .transpose()
        .map { it.sorted() }
        .transpose()
        .pairs()
        .sumOf { (a, b) -> abs(a - b) }

    println(input)
}
