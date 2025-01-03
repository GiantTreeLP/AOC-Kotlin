package year2024

import com.google.auto.service.AutoService
import common.*

@AutoService(AOCSolution::class)
class Day14 : AOCSolution {
    override val year = 2024
    override val day = 14

    private data class Robot(val position: Position, val velocity: Point)

    private fun parseRobot(line: String): Robot {
        val (px, py, vx, vy) = robotRegex.find(line)!!.destructured
        return Robot(Point(px.toLong(), py.toLong()), Point(vx.toLong(), vy.toLong()))
    }

    override fun part1(inputFile: String): String {
        val robots = readResourceLines(inputFile).map(::parseRobot)

        val width = if (inputFile.endsWith("sample")) SAMPLE_WIDTH else WIDTH
        val height = if (inputFile.endsWith("sample")) SAMPLE_HEIGHT else HEIGHT
        val simulationIterations = 100L

        // Simulate the robots moving
        val endRobots = robots.map {
            val simulatedPosition = (it.position + it.velocity * simulationIterations)
            val position = Point(simulatedPosition.x modulo width, simulatedPosition.y modulo height)
            Robot(position, it.velocity)
        }

        // Build the quadrants
        val quadrants = buildSet {
            val qWidth = (width / 2)
            val qHeight = (height / 2)
            add(Point(0, 0)..Point(qWidth, qHeight))
            add(Point(qWidth + 1, 0)..Point(width, qHeight))
            add(Point(0, qHeight + 1)..Point(qWidth, height))
            add(Point(qWidth + 1, qHeight + 1)..Point(width, height))
        }

        // Group the robots by their final quadrant
        // Robots might be in the lines between the quadrants, so we are loosing some robots
        val quadrantRobots = buildMap<Rectangle, MutableList<Robot>> {
            for (robot in endRobots) {
                for (quadrant in quadrants) {
                    if (robot.position in quadrant) {
                        getOrPut(quadrant) { mutableListOf() }.add(robot)
                        break
                    }
                }
            }
        }

        // Count the number of robots in each quadrant
        val quadrantCounts = quadrantRobots.flatMap { listOf(it.value.size.toLong()) }

        // Calculate the product of the counts
        val product = quadrantCounts.product()
        return product.toString()
    }

    override fun part2(inputFile: String): String {
        val robots = readResourceLines(inputFile).map(::parseRobot).toTypedArray()
        val width = if (inputFile.endsWith("sample")) SAMPLE_WIDTH else WIDTH
        val height = if (inputFile.endsWith("sample")) SAMPLE_HEIGHT else HEIGHT

        val windowSize = (width * height).toInt()

        val xPositions = DoubleArray(robots.size)
        val yPositions = DoubleArray(robots.size)

        val variancesX = DoubleArray(windowSize)
        val variancesY = DoubleArray(windowSize)

        // Iterate over frames
        repeat(windowSize) { iteration ->
            // Move the robots to the next frame
            for (index in robots.indices) {
                val robot = robots[index]
                xPositions[index] = ((robot.position.x + iteration * robot.velocity.x) modulo width).toDouble()
                yPositions[index] = ((robot.position.y + iteration * robot.velocity.y) modulo height).toDouble()
            }

            // Calculate the variance of the x and y coordinates
            val xVariance = xPositions.variance()
            val yVariance = yPositions.variance()
            variancesX[iteration] = xVariance
            variancesY[iteration] = yVariance
        }

        // Check if the variance of the x and y coordinates is within 1 standard deviation of the mean
        val xSD = variancesX.standardDeviation() * 5
        val xMean = variancesX.average()
        val ySD = variancesY.standardDeviation() * 5
        val yMean = variancesY.average()

        for ((index, variances) in variancesX.zip(variancesY).withIndex()) {
            val (xVariance, yVariance) = variances
            // Compare the variances to be in 5 standard deviations of the mean
            if (xVariance !in xMean - xSD..xMean + xSD &&
                yVariance !in yMean - ySD..yMean + ySD
            ) {
                // We have found an outlier
                return index.toString()
            }
        }
        return "No outlier found"
    }

    companion object {
        private const val SAMPLE_WIDTH = 11L
        private const val SAMPLE_HEIGHT = 7L

        private const val WIDTH = 101L
        private const val HEIGHT = 103L

        private val robotRegex = Regex("""p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)""")

        private infix fun Long.modulo(divisor: Long): Long {
            val result = this % divisor
            return if (result < 0) result + divisor else result
        }

        private fun Iterable<Long>.product(): Long {
            return this.reduce { acc, i -> acc * i }
        }
    }
}
