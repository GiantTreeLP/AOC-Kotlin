package day14

import common.Grid.Companion.toGrid
import common.Point
import common.readResourceLines
import day14.Part2.addModulo
import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories

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

    val emptyGrid =
        (0 until HEIGHT).map { _ ->
            (0 until WIDTH).map { _ -> Color.BLACK }.toList()
        }.toTypedArray()
            .toList()
            .toGrid()

    // Simulate the robots moving
    var state = robots
    var iterations = 0
    while (true) {
        state = state.map {
            Part2.Robot(it.position.addModulo(it.velocity, WIDTH, HEIGHT), it.velocity)
        }
        iterations++

        val grid = emptyGrid.copy()
        for ((position) in state) {
            grid[position] = Color.WHITE
        }
        BufferedImage(WIDTH.toInt(), HEIGHT.toInt(), BufferedImage.BITMASK).apply {
            this.setRGB(
                0,
                0,
                WIDTH.toInt(),
                HEIGHT.toInt(),
                grid.map { it.third.rgb }.toIntArray(),
                0,
                WIDTH.toInt()
            )
            val outputPath = Path("day14", "frames", "frame-$iterations.png")
            outputPath.createParentDirectories()
            ImageIO.write(this, "png", outputPath.toFile())
        }

        // Wait for the user to press enter
        readln()
    }

    // The answer has to be determined manually by looking at the frames
}