package year2025

import com.google.auto.service.AutoService
import common.AOCSolution
import common.grid.CharGrid
import common.grid.CharGrid.Companion.toCharGrid
import common.readResourceLines

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
                    grid.countPaperRolls(x, y) < 4
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
                        grid.countPaperRolls(x, y) < 4
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
        private const val PAPER_ROLL = '@'
        private const val REMOVED_PAPER_ROLL = 'x'
        private const val RADIUS = 1

        /**
         * Count the amount of paper rolls around the given position.
         *
         * @param startX the horizontal position of the center of the count in the grid
         * @param startY the vertical position of the center of the count in the grid
         */
        private fun CharGrid.countPaperRolls(
            startX: Int,
            startY: Int,
        ): Int {
            var count = 0
            for (y in maxOf(startY - RADIUS, 0)..minOf(startY + RADIUS, height - 1)) {
                for (x in maxOf(startX - RADIUS, 0)..minOf(startX + RADIUS, width - 1)) {
                    if (this[x, y] == PAPER_ROLL) {
                        count++
                    }
                }
            }
            // Assume the center to also be a paper roll and thus subtract one
            return count - 1
        }
    }
}
