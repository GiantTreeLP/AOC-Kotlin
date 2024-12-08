package day4

import common.chunks
import common.primaryDiagonals
import common.printEach
import common.readResource

private val xmasRegex = Regex("""MAS|SAM""")

fun main() {
    val input = readResource("day4/input")

    val grid = input
        .lineSequence()
        .filter { it.isNotEmpty() }
        .map { it.toList() }
        .toList()

    // Iterate each 3x3 grid
    val blocks = grid
        .chunks(3).printEach()
        .map { it.primaryDiagonals() }
        .map { it.map { it.joinToString("") } }
        .map { it.sumOf { xmasRegex.findAll(it).count() } }
        .count { it == 2 }

    println(blocks)
}
