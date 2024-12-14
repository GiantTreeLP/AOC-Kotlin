package day14

import common.Point
import common.Rectangle
import common.readResourceLines
import day14.Part1.modulo
import day14.Part1.product

private const val WIDTH = 101L
private const val HEIGHT = 103L
private const val SIMULATION_ITERATIONS = 100L

private object Part1 {
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

    fun Iterable<Long>.product(): Long {
        return this.reduce { acc, i -> acc * i }
    }

}

fun main() {
    val robots = readResourceLines("day14/input").map(Part1::parseRobot)

    // Simulate the robots moving
    val endRobots = robots.map {
        val simulatedPosition = (it.position + it.velocity * SIMULATION_ITERATIONS)
        val position = Point(simulatedPosition.x modulo WIDTH, simulatedPosition.y modulo HEIGHT)
        Part1.Robot(position, it.velocity)
    }

    // Build the quadrants
    val quadrants = buildSet {
        val qWidth = (WIDTH / 2) - (WIDTH and 1)
        val qHeight = (HEIGHT / 2) - (HEIGHT and 1)
        add(Rectangle(Point(0, 0), Point(qWidth, qHeight)))
        add(Rectangle(Point(qWidth + 1 + (WIDTH and 1), 0), Point(WIDTH, qHeight)))
        add(Rectangle(Point(0, qHeight + 1 + (HEIGHT and 1)), Point(qWidth, HEIGHT)))
        add(Rectangle(Point(qWidth + 1 + (WIDTH and 1), qHeight + 1 + (HEIGHT and 1)), Point(WIDTH, HEIGHT)))
    }

    // Group the robots by their final quadrant
    // Robots might be in the lines between the quadrants, so we are loosing some robots
    val quadrantRobots = buildMap<Rectangle, MutableList<Part1.Robot>> {
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
    println(product)
}