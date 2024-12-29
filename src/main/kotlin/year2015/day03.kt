package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResource

@AutoService(AOCSolution::class)
class Day03 : AOCSolution {
    override val year = 2015
    override val day = 3

    override fun part1(inputFile: String): String {
        val input = readResource(inputFile)

        var x = 0
        var y = 0
        val visited = mutableMapOf<Pair<Int, Int>, Int>()
        visited[x to y] = 1

        for (c in input) {
            when (c) {
                '^' -> y++
                'v' -> y--
                '<' -> x--
                '>' -> x++
            }
            visited.merge(x to y, 1, Int::plus)
        }

        return visited.size.toString()
    }

    override fun part2(inputFile: String): String {
        val input = readResource(inputFile)

        var santaX = 0
        var santaY = 0
        var robotX = 0
        var robotY = 0
        val visited = mutableMapOf<Pair<Int, Int>, Int>()
        visited[santaX to santaY] = 2

        for (i in input.indices step 2) {
            when (input[i]) {
                '^' -> santaY++
                'v' -> santaY--
                '<' -> santaX--
                '>' -> santaX++
            }
            visited.merge(santaX to santaY, 1, Int::plus)

            when (input[i + 1]) {
                '^' -> robotY++
                'v' -> robotY--
                '<' -> robotX--
                '>' -> robotX++
            }
            visited.merge(robotX to robotY, 1, Int::plus)
        }

        return visited.size.toString()
    }

}