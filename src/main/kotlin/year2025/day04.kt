package year2025

import com.google.auto.service.AutoService
import common.AOCSolution
import common.grid.CharGrid
import common.grid.CharGrid.Companion.toCharGrid
import common.readResourceLines
import java.util.function.Predicate

@AutoService(AOCSolution::class)
class Day04 : AOCSolution {
    override val year = 2025
    override val day = 4

    override fun part1(inputFile: String): String {
        val grid = readResourceLines(inputFile).map(CharSequence::toList).toCharGrid()

        var accessiblePaperRolls = 0

        // Quickly iterate the grid in top-left to bottom-right order
        for (y in 0 until grid.height) {
            for (x in 0 until grid.width) {
                // Count the neighbours of each paper roll.
                if (grid[x, y] == PAPER_ROLL &&
                    grid.countNeighbours(x, y, 1) { it == PAPER_ROLL } < 4
                ) {
                    accessiblePaperRolls++
                }
            }
        }
        return accessiblePaperRolls.toString()
    }

    override fun part2(inputFile: String): String {
        val grid = readResourceLines(inputFile).map(CharSequence::toList).toCharGrid()

        var count = 0
        while (true) {
            var iterationCount = 0

            // Quickly iterate the grid in top-left to bottom-right order
            for (y in 0 until grid.height) {
                for (x in 0 until grid.width) {
                    if (grid[x, y] == PAPER_ROLL &&
                        grid.countNeighbours(x, y, 1) { it == PAPER_ROLL } < 4
                    ) {
                        // Remove the paper roll for the next iteration
                        grid[x, y] = REMOVED_PAPER_ROLL
                        iterationCount++
                    }
                }
            }
            count += iterationCount

            // Repeat the count until no paper rolls are accessible.
            if (iterationCount == 0) {
                break
            }
        }

        return count.toString()
    }

    private companion object {
        const val PAPER_ROLL = '@'
        const val REMOVED_PAPER_ROLL = 'x'

        /**
         * Count the neighbours of the given cell in the given [radius] of cells that satisfy the given predicate.
         *
         * @param startX the horizontal position of the center of the count in the grid
         * @param startY the vertical position of the center of the count in the grid
         * @param radius the radius counted in Manhattan distance to the center
         * @param predicate the test that needs to pass for a neighbour to count.
         */
        private fun CharGrid.countNeighbours(
            startX: Int,
            startY: Int,
            radius: Int,
            predicate: Predicate<Char>,
        ): Int {
            var count = 0
            for (y in maxOf(startY - radius, 0)..minOf(startY + radius, height - 1)) {
                for (x in maxOf(startX - radius, 0)..minOf(startX + radius, width - 1)) {
                    if (y == startY && x == startX) {
                        continue
                    }
                    if (predicate.test(this[x, y])) {
                        count++
                    }
                }
            }
            return count
        }
    }
}
