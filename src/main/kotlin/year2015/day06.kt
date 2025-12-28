package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.Point
import common.Rectangle
import common.Rectangle.Companion.Rectangle
import common.readResourceLines
import kotlin.math.max

@AutoService(AOCSolution::class)
class Day06 : AOCSolution {
    override val year = 2015
    override val day = 6

    override fun part1(inputFile: String): String {
        val instructions = readResourceLines(inputFile).toTypedArray()

        val lights = Array(GRID_SIZE) { BooleanArray(GRID_SIZE) }

        instructions.forEach { instruction ->
            val region = parseRegion(instruction)
            when (instruction[RELEVANT_INDEX]) {
                TURN_ON -> {
                    for (y in region.topLeft.y.toInt() until region.bottomRight.y.toInt()) {
                        for (x in region.topLeft.x.toInt() until region.bottomRight.x.toInt()) {
                            lights[x][y] = true
                        }
                    }
                }

                TURN_OFF -> {
                    for (y in region.topLeft.y.toInt() until region.bottomRight.y.toInt()) {
                        for (x in region.topLeft.x.toInt() until region.bottomRight.x.toInt()) {
                            lights[x][y] = false
                        }
                    }
                }

                TOGGLE -> {
                    for (y in region.topLeft.y.toInt() until region.bottomRight.y.toInt()) {
                        for (x in region.topLeft.x.toInt() until region.bottomRight.x.toInt()) {
                            lights[x][y] = !lights[x][y]
                        }
                    }
                }
            }
        }

        return lights.sumOf { it.count { it } }.toString()
    }

    private fun parseRegion(instruction: String): Rectangle {
        val parts = instruction.split(" ")

        val start = parts[parts.size - 3].split(",")
        val end = parts[parts.size - 1].split(",")

        return Rectangle(Point(start[0].toLong(), start[1].toLong()), Point(end[0].toLong() + 1, end[1].toLong() + 1))
    }

    override fun part2(inputFile: String): String {
        val instructions = readResourceLines(inputFile).toTypedArray()

        val lights = Array(GRID_SIZE) { IntArray(GRID_SIZE) }

        instructions.forEach { instruction ->
            val region = parseRegion(instruction)
            when (instruction[RELEVANT_INDEX]) {
                TURN_ON -> {
                    for (y in region.topLeft.y.toInt() until region.bottomRight.y.toInt()) {
                        for (x in region.topLeft.x.toInt() until region.bottomRight.x.toInt()) {
                            lights[x][y] += 1
                        }
                    }
                }

                TURN_OFF -> {
                    for (y in region.topLeft.y.toInt() until region.bottomRight.y.toInt()) {
                        for (x in region.topLeft.x.toInt() until region.bottomRight.x.toInt()) {
                            lights[x][y] = max(0, lights[x][y] - 1)
                        }
                    }
                }

                TOGGLE -> {
                    for (y in region.topLeft.y.toInt() until region.bottomRight.y.toInt()) {
                        for (x in region.topLeft.x.toInt() until region.bottomRight.x.toInt()) {
                            lights[x][y] += 2
                        }
                    }
                }
            }
        }

        return lights.sumOf { it.sum() }.toString()
    }

    companion object {
        private const val GRID_SIZE = 1000
        private const val RELEVANT_INDEX = 6
        private const val TURN_ON = 'n'
        private const val TURN_OFF = 'f'
        private const val TOGGLE = ' '
    }
}
