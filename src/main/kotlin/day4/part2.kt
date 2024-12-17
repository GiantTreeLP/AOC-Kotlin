package day4

import common.chunks
import common.primaryDiagonals
import common.readResourceLines

private val xmasRegex = Regex("""MAS|SAM""")

fun main() {
    val input = readResourceLines("day4/input")

    val grid = input
        .filter { it.isNotEmpty() }
        .map { it.toList() }

    // Iterate each 3x3 grid
    val blocks = grid
        .chunks(3)
        .map { it.primaryDiagonals() }
        .map { it.map { it.joinToString("") } }
        .map { it.sumOf { xmasRegex.findAll(it).count() } }
        .count { it == 2 }

    println(blocks)
}
