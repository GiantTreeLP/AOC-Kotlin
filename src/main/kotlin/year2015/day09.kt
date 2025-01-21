package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.IndexPermutationIterator
import common.readResourceLines
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

@AutoService(AOCSolution::class)
class Day09 : AOCSolution {
    override val year = 2015
    override val day = 9

    private inline fun solve(inputFile: String, initialValue: Int, comparisonFunction: (Int, Int) -> Int): String {
        val inputDistances = readResourceLines(inputFile)

        // Number of cities can be calculated by solving the equation:
        // (N * (N + 1)) / 2 = M
        // N * (N + 1) = 2M
        // N^2 + N = 2M
        // 1N^2 + 1N - 2M = 0 -> quadratic equation, use the abc formula
        // n1,2 = (-1 +- sqrt(1^2 - 4 * 1 * -2M)) / 2 * 1
        // n1,2 = (-1 +- sqrt(1 - -8M)) / 2
        // n1,2 = (-1 +- sqrt(8M + 1)) / 2
        // Take the positive value, since we can't have a negative number of cities
        // N = (sqrt(8M + 1) - 1) / 2
        // In this case, M is the number of lines in the input file
        // Since the distances are bidirectional, add 1 to the result (i.e. the first city)
        // Number of Cities = (sqrt(8 * lines + 1) - 1) / 2 + 1
        val citiesCount = (sqrt(8.0 * inputDistances.size + 1).toInt() - 1) / 2 + 1
        val distances = IntArray(citiesCount * citiesCount) { 0 }

        val cities = mutableMapOf<String, Int>()
        inputDistances.forEach { line ->
            // City1 to City2 = Distance
            val (from, _, to, _, distanceStr) = line.split(" ")
            val distance = distanceStr.toInt()

            val fromIndex = cities.getOrPut(from) { cities.size }
            val toIndex = cities.getOrPut(to) { cities.size }

            cities[from] = fromIndex
            cities[to] = toIndex

            distances[toIndex * citiesCount + fromIndex] = distance
            distances[fromIndex * citiesCount + toIndex] = distance
        }

        var distance = initialValue
        IndexPermutationIterator(citiesCount).forEach { path ->
            var currentDistance = 0
            // Connect each city in the path
            for (i in 0 until path.size - 1) {
                currentDistance += distances[path[i] * citiesCount + path[i + 1]]
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
