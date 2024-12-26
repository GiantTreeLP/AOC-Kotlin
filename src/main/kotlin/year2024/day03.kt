package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResource

@AutoService(AOCSolution::class)
class Day03 : AOCSolution {
    override val year = 2024
    override val day = 3

    override fun part1(inputFile: String): String {
        val input = readResource(inputFile)

        val sum = mulRegex.findAll(input).sumOf { result ->
            val (a, b) = result.destructured
            a.toInt() * b.toInt()
        }

        return sum.toString()
    }

    override fun part2(inputFile: String): String {
        val input = readResource(inputFile)

        val sum = mulRegex.findAll(input).sumOf { result ->
            val index = result.range.first

            // Find the last occurrence of "do()" and "don't()" before the current mul expression
            val doIndex = input.lastIndexOf("do()", index)
            val dontIndex = input.lastIndexOf("don't()", index)

            // If there is no "do()" or "don't()" before the current mul expression, or if "do()" is after "don't()"
            // then calculate the multiplication
            if ((doIndex == -1 && dontIndex == -1) || doIndex > dontIndex) {
                val (a, b) = result.destructured
                a.toInt() * b.toInt()
            } else {
                0
            }
        }

        return sum.toString()
    }

    companion object {
        private val mulRegex = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")
    }
}