package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceLines
import common.spaceSplitRegex
import kotlin.math.abs

@AutoService(AOCSolution::class)
class Day01 : AOCSolution {
    override val year = 2024
    override val day = 1

    private fun readIntegerColumns(inputFile: String): Pair<IntArray, IntArray> {
        val input = readResourceLines(inputFile)

        val array1 = IntArray(input.size)
        val array2 = IntArray(input.size)

        // Convert and transpose the input in one step
        for (i in input.indices) {
            val (left, right) = input[i].split(spaceSplitRegex, 2)
            array1[i] = left.toInt()
            array2[i] = right.toInt()
        }
        return Pair(array1, array2)
    }

    override fun part1(inputFile: String): String {
        val (array1, array2) = readIntegerColumns(inputFile)
        array1.sort()
        array2.sort()

        var result = 0
        for (i in array1.indices) {
            result += abs(array1[i] - array2[i])
        }

        return result.toString()
    }

    override fun part2(inputFile: String): String {
        val (leftList, rightList) = readIntegerColumns(inputFile)

        val rightGrouping = rightList
            .groupingBy { it }
            .eachCount()
        val result = leftList.sumOf { left -> left * rightGrouping.getOrDefault(left, 0) }

        return result.toString()
    }

    companion object {
        private inline fun <K> IntArray.groupingBy(crossinline keySelector: (Int) -> K): Grouping<Int, K> {
            return object : Grouping<Int, K> {
                override fun sourceIterator(): Iterator<Int> = this@groupingBy.iterator()
                override fun keyOf(element: Int): K = keySelector(element)
            }
        }
    }
}