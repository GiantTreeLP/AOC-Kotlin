package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.grid.toGrid
import common.pairs
import common.readResourceLines
import common.transpose
import java.util.Collections
import kotlin.math.abs

@AutoService(AOCSolution::class)
class Day01 : AOCSolution {
    override val year = 2024
    override val day = 1

    override fun part1(inputFile: String): String {
        val lists = readResourceLines(inputFile)
            .map { it.split(common.spaceSplitRegex).map(String::toInt) }
            .transpose()
            .onEach { it.sorted() }

        val (list1, list2) = lists

        return list1.zip(list2) { a, b -> abs(a - b) }.sum()
            .toString()
    }

    override fun part2(inputFile: String): String {
        val lists = readResourceLines(inputFile)
            .map { it.split(common.spaceSplitRegex).map(String::toInt) }
            .transpose()

        val leftList = lists[0]
        val rightList = lists[1]
            .groupingBy { it }
            .eachCount()
        val result = leftList.sumOf { left -> left * rightList.getOrDefault(left, 0) }

        return result.toString()
    }
}