package year2025

import com.google.auto.service.AutoService
import common.AOCSolution
import common.grid.CharGrid.Companion.toCharGrid
import common.grid.rows
import common.grid.toGrid
import common.indexNotOf
import common.readResourceLines
import common.spaceSplitRegex
import mapToLongArray

@AutoService(AOCSolution::class)
class Day06 : AOCSolution {
    override val year = 2025
    override val day = 6

    override fun part1(inputFile: String): String {
        val worksheet = readResourceLines(inputFile)
        // Arrange the problem in a grid and transpose it, so that the operation is the last element of each row
        val problems = worksheet.map { line -> line.trim().split(spaceSplitRegex) }.toGrid().transposed()

        val grandTotal = problems.rows().sumOf { line ->
            // Map the all but the last element to a number
            val numbers = line.mapToLongArray(0, line.lastIndex - 1, String::toLong)
            // Extract the operation
            val operation = line.last()[0]

            // Call the correct reduction
            // The "else" branch is needed for the compiler
            when (operation) {
                ADD -> numbers.sum()
                MULTIPLY -> numbers.reduce { acc, value -> acc * value }
                else -> 0
            }
        }
        return grandTotal.toString()
    }

    override fun part2(inputFile: String): String {
        val worksheet = readResourceLines(inputFile)

        // In this part the problem is more complicated and dependent on the individual characters.
        val charGrid = worksheet.map(CharSequence::toList).toCharGrid().transposed()

        val numbers = mutableListOf<Long>()
        val sb = StringBuilder(charGrid.width)

        val problems = buildList {
            // Begin with an empty operation
            // Assume the operation will be set to a valid value
            var operation = SPACE
            for (y in 0 until charGrid.height) {
                // Extract each row (transposed column)
                sb.clear().append(charGrid[y])
                // Find the bounds of the number
                val numberOffset = sb.indexNotOf(SPACE)

                if (numberOffset != -1) {
                    // A number was found, parse it and add it to the list.
                    val endIndex = sb.indexOfAny(STOP_CHARACTERS, numberOffset + 1)
                    val number = java.lang.Long.parseLong(sb, numberOffset, endIndex, 10)
                    numbers.add(number)

                    // Check whether there is an operation in the last column.
                    // IF so, that's the next relevant operation
                    val lastColumn = sb[sb.lastIndex]
                    if (lastColumn != SPACE) {
                        operation = lastColumn
                    }
                } else {
                    // No number was found, that's the separator for two calculations.
                    // Finalize the collection and clear the numbers.
                    // `toLongArray` creates a neat copy of the Longs in the list.
                    add(Problem(operation, numbers.toLongArray()))
                    numbers.clear()
                }
            }
            // Add the last remaining problem to the list
            add(Problem(operation, numbers.toLongArray()))
        }

        // Reduce all problems to their solutions and sum them up.
        val grandTotal = problems.sumOf { problem ->
            when (problem.operation) {
                ADD -> problem.numbers.sum()
                MULTIPLY -> problem.numbers.reduce { acc, value -> acc * value }
                else -> 0
            }
        }

        return grandTotal.toString()
    }

    private companion object {
        private const val ADD = '+'
        private const val MULTIPLY = '*'
        private const val SPACE = ' '
        private val STOP_CHARACTERS = charArrayOf(SPACE, ADD, MULTIPLY)

        @JvmRecord
        @Suppress("ArrayInDataClass")
        private data class Problem(val operation: Char, val numbers: LongArray)
    }
}
