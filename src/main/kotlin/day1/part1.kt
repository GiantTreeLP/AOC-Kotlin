package day1

import common.*
import kotlin.math.abs

fun main() {
    val input = readResourceLines("day1/input")
        .map { it.split(splitRegex) }
        .map { it.map(String::toInt) }
        .transpose()
        .map { it.sorted() }
        .transpose()
        .pairs()
        .sumOf { (a, b) -> abs(a - b) }

    println(input)
}
