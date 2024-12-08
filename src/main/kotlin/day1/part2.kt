package day1

import common.getResourceAsStream
import common.pairs
import common.splitRegex
import common.transpose
import kotlin.math.abs

fun main() {
    val lists = getResourceAsStream("day1/input")
        .bufferedReader()
        .readLines()
        .map { it.split(splitRegex) }
        .map { it.map { it.toInt() } }
        .transpose()
    val list1 = lists[0]
    val list2 = lists[1]
    var result = list1.sumOf { left -> left * list2.count { it == left } }

    println(result)
}
