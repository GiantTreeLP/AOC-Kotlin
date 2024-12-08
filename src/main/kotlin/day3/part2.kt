package day3

import common.readResource


fun main() {
    val mulRegex = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")
    val input = readResource("day3/input")

    val sum = mulRegex.findAll(input).sumOf { result ->
        val index = result.range.first

        val doIndex = input.lastIndexOf("do()", index)
        val dontIndex = input.lastIndexOf("don't()", index)
        if ((doIndex == -1 && dontIndex == -1) || doIndex > dontIndex) {
            val (a, b) = result.destructured
            a.toInt() * b.toInt()
        } else {
            0
        }
    }

    println(sum)
}
