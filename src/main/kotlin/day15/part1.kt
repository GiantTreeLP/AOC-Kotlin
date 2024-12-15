package day15

import common.Grid
import common.Grid.Companion.toGrid
import common.Point
import common.readResource
import day15.Part1.directions
import day15.Part1.findRobot
import day15.Part1.moveCell

private val splitRegex = Regex("""\r?\n\r?\n""")
private val lineRegex = Regex("""\r?\n""")

private object Part1 {
    val directions = mapOf(
        '^' to Point.UP,
        '<' to Point.LEFT,
        'v' to Point.DOWN,
        '>' to Point.RIGHT
    )

    sealed class Cell(var position: Point) {
        class Wall(position: Point) : Cell(position)
        class Empty(position: Point) : Cell(position)
        class Box(position: Point) : Cell(position)
        class Robot(position: Point) : Cell(position)

        override fun toString(): String {
            return when (this) {
                is Wall -> "#"
                is Empty -> "."
                is Box -> "O"
                is Robot -> "@"
            }
        }
    }

    fun findRobot(map: Grid<Cell>) =
        map.first { it.third is Cell.Robot }.third as Cell.Robot

    fun moveCell(map: Grid<Cell>, cell: Cell, direction: Point): Boolean {
        val newPosition = cell.position + direction

        if (newPosition !in map.bounds) {
            return false
        }

        when (val newCell = map[newPosition]) {
            // A wall prevents movement
            is Cell.Wall -> {
                return false
            }
            // A box can be pushed and needs to check, whether it can be pushed
            // Update the map
            is Cell.Box -> {
                val canMove = moveCell(map, newCell, direction)
                if (canMove) {
                    map[cell.position] = Cell.Empty(cell.position)
                    map[newPosition] = cell.apply { position = newPosition }
                    return true
                } else {
                    return false
                }
            }

            is Cell.Empty -> {
                // Update the map
                map[cell.position] = Cell.Empty(cell.position)
                map[newPosition] = cell.apply { position = newPosition }
                return true
            }

            is Cell.Robot -> throw IllegalStateException("Robot cannot be moved by itself")
        }
    }
}

fun main() {
    val (inputMap, moves) = readResource("day15/input").split(splitRegex)

    val map = inputMap.split(lineRegex).mapIndexed { y, row ->
        row.mapIndexed { x, cell ->
            when (cell) {
                '#' -> Part1.Cell.Wall(Point(x.toLong(), y.toLong()))
                '.' -> Part1.Cell.Empty(Point(x.toLong(), y.toLong()))
                'O' -> Part1.Cell.Box(Point(x.toLong(), y.toLong()))
                '@' -> Part1.Cell.Robot(Point(x.toLong(), y.toLong()))
                else -> error("Unknown cell type: $cell")
            }
        }
    }.toGrid()

    var robot = findRobot(map)
    val movementDirections = moves.replace("\n", "").map { directions[it]!! }

    for (direction in movementDirections) {
        moveCell(map, robot, direction)
        robot = findRobot(map)
    }

    println(map)

    val boxesSum = map.sumOf {
        if (it.third !is Part1.Cell.Box) {
            0
        } else {
            it.third.position.x + it.third.position.y * 100
        }
    }

    println(boxesSum)
}
