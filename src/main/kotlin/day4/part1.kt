package day4

import common.readResourceLines

private val xmasRegex = Regex("""XMAS""")

fun main() {
    val input = readResourceLines("day4/input")

    val lines = input.filter { it.isNotEmpty() }.map { it.toCharArray() }

    val grid = buildGrids(lines)

    val count = grid
        .map { it.map { xmasRegex.findAll(it).count() } }
        .flatten()
        .sum()

    println(count)
}

private fun buildGrids(charArrays: List<CharArray>): List<List<String>> {
    // Require the grid to be a square
    require(charArrays.all { it.size == charArrays.size }) { "Grid must be a square" }

    val grids = mutableListOf<List<String>>()

    // Horizontal
    val horizontal = charArrays.map { String(it) }
    grids.add(horizontal)
    // Horizontal reversed
    grids.add(horizontal.map(String::reversed))

    // Vertical
    val vertical = charArrays[0].indices.map { i -> charArrays.map { it[i] }.joinToString("") }
    grids.add(vertical)
    // Vertical reversed
    grids.add(vertical.map(String::reversed))

    // Diagonal
    val diagonalTL = mutableListOf<String>()
    // Top left to bottom right
    // Rows
    for (startRow in charArrays.indices) {
        var x = 0
        var y = startRow
        val diagonal = mutableListOf<Char>()
        while (y < charArrays.size) {
            diagonal.add(charArrays[y][x])
            x++
            y++
        }
        diagonalTL.add(diagonal.joinToString(""))
    }

    // Columns
    for (startColumn in 1 until charArrays.size) {
        var x = startColumn
        var y = 0
        val diagonal = mutableListOf<Char>()
        while (x < charArrays.size) {
            diagonal.add(charArrays[y][x])
            x++
            y++
        }
        diagonalTL.add(diagonal.joinToString(""))
    }

    grids.add(diagonalTL)
    // Diagonal reversed
    grids.add(diagonalTL.map(String::reversed))

    // Top right to bottom left
    val diagonalTR = mutableListOf<String>()
    // Rows
    for (startRow in charArrays.indices) {
        var x = charArrays.size - 1
        var y = startRow
        val diagonal = mutableListOf<Char>()
        while (y < charArrays.size) {
            diagonal.add(charArrays[y][x])
            x--
            y++
        }
        diagonalTR.add(diagonal.joinToString(""))
    }

    // Columns
    for (startColumn in charArrays.size - 2 downTo 0) {
        var x = startColumn
        var y = 0
        val diagonal = mutableListOf<Char>()
        while (x >= 0) {
            diagonal.add(charArrays[y][x])
            x--
            y++
        }
        diagonalTR.add(diagonal.joinToString(""))
    }

    grids.add(diagonalTR)
    // Diagonal reversed
    grids.add(diagonalTR.map(String::reversed))

    return grids
}
