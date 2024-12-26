package year2024

import com.google.auto.service.AutoService
import common.*
import common.Grid.Companion.toGrid

@AutoService(AOCSolution::class)
class Day04 : AOCSolution {
    override val year = 2024
    override val day = 4

    private fun buildGrids(inputGrid: Grid<Char>): List<List<String>> {
        val grids = mutableListOf<List<String>>()

        // Horizontal
        val horizontal = inputGrid.rows().map { it.joinToString("") }
        grids.add(horizontal)
        // Horizontal reversed
        grids.add(horizontal.map(String::reversed))

        // Vertical
        val vertical = inputGrid.columns().map { it.joinToString("") }
        grids.add(vertical)
        // Vertical reversed
        grids.add(vertical.map(String::reversed))

        // Diagonal
        val diagonalTL = mutableListOf<String>()
        for (x in 0 until inputGrid.width) {
            diagonalTL.add(inputGrid.diagonal(x).joinToString(""))
        }
        for (y in 1 until inputGrid.height) {
            diagonalTL.add(inputGrid.diagonal(-y).joinToString(""))
        }

        grids.add(diagonalTL)
        // Diagonal reversed
        grids.add(diagonalTL.map(String::reversed))

        // Top right to bottom left
        val reversedGrid = inputGrid.flipVertical()
        val diagonalTR = buildList(reversedGrid.width + reversedGrid.height - 1) {
            for (x in 0 until reversedGrid.width) {
                add(reversedGrid.diagonal(x).joinToString(""))
            }
            for (y in 1 until reversedGrid.height) {
                add(reversedGrid.diagonal(-y).joinToString(""))
            }
        }

        grids.add(diagonalTR)
        // Diagonal reversed
        grids.add(diagonalTR.map(String::reversed))

        return grids
    }


    override fun part1(inputFile: String): String {
        val input = readResourceLines(inputFile)

        val lines = input
            .map { it.toList() }
            .toGrid()

        val grid = buildGrids(lines)

        val count = grid
            .map { order -> order.map { slice -> xmasRegex.findAll(slice).count() } }
            .flatten()
            .sum()

        return count.toString()
    }

    override fun part2(inputFile: String): String {
        val input = readResourceLines(inputFile)

        val grid = input
            .filter { it.isNotEmpty() }
            .map { it.toList() }
            .toGrid()

        // Iterate each 3x3 grid
        val blocks = grid
            .subGrids(3, 3)
            .map(Grid<Char>::primaryDiagonals)
            .map { diagonals -> diagonals.map { diagonalChars -> diagonalChars.joinToString("") } }
            .map { diagonals -> diagonals.sumOf { diagonal -> masRegex.findAll(diagonal).count() } }
            .count { it == 2 }

        return blocks.toString()
    }

    companion object {
        private val xmasRegex = Regex("""XMAS""")
        private val masRegex = Regex("""MAS|SAM""")
    }
}