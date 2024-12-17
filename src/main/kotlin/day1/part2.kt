package day1

import common.*

fun main() {
    val lists = readResourceLines("day1/input")
        .map { it.split(splitRegex) }
        .map { it.map(String::toInt) }
        .transpose()
    val leftList = lists[0]
    val rightList = lists[1]
    val result = leftList.sumOf { left -> left * rightList.count { it == left } }

    println(result)
}
