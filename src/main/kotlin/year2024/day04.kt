package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.count
import common.grid.*
import common.mapArray
import common.readResourceLines

@AutoService(AOCSolution::class)
class Day04 : AOCSolution {
    override val year = 2024
    override val day = 4

    private fun buildGrids(inputGrid: Grid<Char>): Array<String> {


        // Horizontal
        val horizontal = inputGrid.toStrings()

        // Vertical
        val vertical = inputGrid.transposed().toStrings()

        // Diagonal
        val diagonalTL = Array(inputGrid.width + inputGrid.height - 1) { index ->
            inputGrid.diagonal(index - inputGrid.height + 1).joinToString("")
        }

        // Top right to bottom left
        val reversedGrid = inputGrid.flipVertical()
        val diagonalTR = Array(reversedGrid.width + reversedGrid.height - 1) { index ->
            reversedGrid.diagonal(index - reversedGrid.height + 1).joinToString("")
        }

        val result = arrayOfNulls<String>(
            2 * inputGrid.width + 2 * inputGrid.height +
                    4 * (inputGrid.width + inputGrid.height - 1)
        )

        val width = inputGrid.width
        val height = inputGrid.height
        val diagonalLength = width + height - 1

        horizontal.copyInto(result, 0)
        horizontal.mapArray(String::reversed).copyInto(result, height)

        vertical.copyInto(result, 2 * height)
        vertical.mapArray(String::reversed).copyInto(result, 2 * height + width)

        diagonalTL.copyInto(result, 2 * height + 2 * width)
        diagonalTL.mapArray(String::reversed).copyInto(result, 2 * height + 2 * width + diagonalLength)

        diagonalTR.copyInto(result, 2 * height + 2 * width + 2 * diagonalLength)
        diagonalTR.mapArray(String::reversed).copyInto(result, 2 * height + 2 * width + 3 * diagonalLength)

        // We can safely cast the array to Array<String> because we know that all elements are non-null
        @Suppress("UNCHECKED_CAST")
        return result as Array<String>
    }


    override fun part1(inputFile: String): String {
        val input = readResourceLines(inputFile)

        val lines = input.map { it.toList() }.toGrid()

        val grid = buildGrids(lines)

        val count = grid.sumOf {
            var count = 0
            var lastIndex = 0
            while (lastIndex != -1) {
                lastIndex = it.indexOf(XMAS, lastIndex)
                if (lastIndex != -1) {
                    count++
                    lastIndex += XMAS.length
                }
            }
            count
        }

        return count.toString()
    }

    override fun part2(inputFile: String): String {
        val input = readResourceLines(inputFile)

        val grid = input.map { it.toList() }.toGrid()

        // Iterate each 3x3 grid
        val blocks = grid
            .subGrids(3, 3)
            .count { subGrid ->
                subGrid.primaryDiagonals().count { diagonal ->
                    diagonal[1] == 'A' &&
                            ((diagonal[0] == 'M' && diagonal[2] == 'S') ||
                                    (diagonal[0] == 'S' && diagonal[2] == 'M'))
                } == 2
            }

        return blocks.toString()
    }

    companion object {
        private const val XMAS = "XMAS"
    }
}