package day14

import common.Grid.Companion.toGrid
import common.Point
import common.readResourceLines
import common.standardDeviation
import common.variance
import day14.Part2.addModulo

private const val WIDTH = 101L
private const val HEIGHT = 103L

private object Part2 {
    val robotRegex = Regex("""p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)""")

    data class Robot(val position: Point, val velocity: Point)

    fun parseRobot(line: String): Robot {
        val (px, py, vx, vy) = robotRegex.find(line)!!.destructured
        return Robot(Point(px.toLong(), py.toLong()), Point(vx.toLong(), vy.toLong()))
    }

    infix fun Long.modulo(divisor: Long): Long {
        val result = this % divisor
        return if (result < 0) result + divisor else result
    }

    fun Point.addModulo(other: Point, width: Long, height: Long): Point {
        return Point((x + other.x) modulo width, (y + other.y) modulo height)
    }
}

fun main() {
    val robots = readResourceLines("day14/input").map(Part2::parseRobot)

    // Simulate the robots moving
    var state = robots
    var iterations = 0
    outer@ while (true) {
        val variancesX = mutableListOf<Double>()
        val variancesY = mutableListOf<Double>()
        val states = mutableListOf<List<Part2.Robot>>()
        // Iterate over 10000 frames
        val windowSize = 10000
        repeat(windowSize) {
            state = state.map {
                Part2.Robot(it.position.addModulo(it.velocity, WIDTH, HEIGHT), it.velocity)
            }
            iterations++

            states.add(state)

            // Calculate the variance of the x and y coordinates
            val xVariance = state.map { it.position.x }.variance()
            val yVariance = state.map { it.position.y }.variance()
            variancesX.add(xVariance)
            variancesY.add(yVariance)
        }

        // Check if the variance of the x and y coordinates is within 1 standard deviation of the mean
        val xSD = variancesX.standardDeviation() * 5
        val xMean = variancesX.average()
        val ySD = variancesY.standardDeviation() * 5
        val yMean = variancesY.average()

        for ((index, variances) in variancesX.zip(variancesY).withIndex()) {
            val actualIndex = index + iterations - windowSize
            val (xVariance, yVariance) = variances
            // Compare the variances to be in 5 standard deviations of the mean
            if (xVariance !in xMean - xSD..xMean + xSD &&
                yVariance !in yMean - ySD..yMean + ySD
            ) {
                // We have found an outlier
                println("Outlier found at iteration $actualIndex")
                val grid = buildList {
                    for (y in 0 until HEIGHT) {
                        add(buildList {
                            for (x in 0 until WIDTH) {
                                add('.')
                            }
                        })
                    }
                }.toGrid()

                for ((position) in states[index]) {
                    grid[position] = '#'
                }

                println(grid)
                println("Mean x: $xMean, Mean y: $yMean")
                println("SD x: $xSD, SD y: $ySD")
                println("Variance x: $xVariance, Variance y: $yVariance")
                println("Index: $actualIndex")
                println()
                break@outer
            }
        }
    }
}