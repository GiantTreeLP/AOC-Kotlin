package day3

import common.readResource


fun main() {
    val mulRegex = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")
    val input = readResource("day3/input")

    val sum = mulRegex.findAll(input).sumOf { result ->
        val (a, b) = result.destructured
        a.toInt() * b.toInt()
    }

    println(sum)
}
