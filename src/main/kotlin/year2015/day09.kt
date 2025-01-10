package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.permutations
import common.readResourceLines
import kotlin.math.max
import kotlin.math.min

@AutoService(AOCSolution::class)
class Day09 : AOCSolution {
    override val year = 2015
    override val day = 9

    private fun solve(inputFile: String, initialValue: Int, comparisonFunction: (Int, Int) -> Int): String {
        val inputDistances = readResourceLines(inputFile)

        val cities = mutableSetOf<String>()
        val distances = buildMap {
            inputDistances.forEach { line ->
                // City1 to City2 = Distance
                val (from, _, to, _, distance) = line.split(" ")
                put(from to to, distance.toInt())
                put(to to from, distance.toInt())
                cities.add(from)
                cities.add(to)
            }
        }

        var distance = initialValue
        cities.permutations().forEach { path ->
            var currentDistance = 0
            // Connect each city in the path
            for (i in 0 until path.size - 1) {
                currentDistance += distances.getValue(path[i] to path[i + 1])
            }
            distance = comparisonFunction(distance, currentDistance)
        }

        return distance.toString()
    }

    override fun part1(inputFile: String): String {
        return solve(inputFile, Int.MAX_VALUE, ::min)
    }

    override fun part2(inputFile: String): String {
        return solve(inputFile, Int.MIN_VALUE, ::max)
    }

}
