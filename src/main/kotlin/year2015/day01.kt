package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResource

@AutoService(AOCSolution::class)
class Day01 : AOCSolution {
    override val year = 2015
    override val day = 1

    override fun part1(inputFile: String): String {
        val input = readResource(inputFile)

        var currentFloor = 0

        for (c in input) {
            when (c) {
                '(' -> {
                    currentFloor++
                }

                ')' -> {
                    currentFloor--
                }
            }
        }

        return currentFloor.toString()
    }

    override fun part2(inputFile: String): String {
        val input = readResource(inputFile)

        var currentFloor = 0

        for (i in input.indices) {
            val c = input[i]
            when (c) {
                '(' -> {
                    currentFloor++
                }

                ')' -> {
                    currentFloor--
                }
            }
            if (currentFloor == -1) {
                return (i + 1).toString()
            }
        }

        return "Not found"
    }
}