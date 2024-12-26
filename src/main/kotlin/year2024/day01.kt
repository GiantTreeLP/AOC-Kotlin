package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.pairs
import common.readResourceLines
import common.transpose
import kotlin.math.abs

@AutoService(AOCSolution::class)
class Day01 : AOCSolution {
    override val year = 2024
    override val day = 1

    override fun part1(inputFile: String): String {
        return readResourceLines(inputFile)
            .map { it.split(common.spaceSplitRegex) }
            .map { it.map(String::toInt) }
            .transpose()
            .map { it.sorted() }
            .transpose()
            .pairs()
            .sumOf { (a, b) -> abs(a - b) }
            .toString()
    }

    override fun part2(inputFile: String): String {
        val lists = readResourceLines(inputFile)
            .map { it.split(common.spaceSplitRegex) }
            .map { it.map(String::toInt) }
            .transpose()
        val leftList = lists[0]
        val rightList = lists[1]
        val result = leftList.sumOf { left -> left * rightList.count { it == left } }

        return result.toString()
    }
}