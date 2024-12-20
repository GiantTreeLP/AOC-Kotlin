package common

interface AOCSolution {
    val year: Int
    val day: Int

    fun part1(inputFile: String): String
    fun part2(inputFile: String): String
}