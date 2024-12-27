package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.grid.Grid
import common.grid.toGrid
import common.Point
import common.readResourceTwoParts

@AutoService(AOCSolution::class)
class Day15 : AOCSolution {
    override val year = 2024
    override val day = 15

    private sealed class Cell(var position: Point) {
        class Wall(position: Point) : Cell(position) {
            override fun toString() = "#"
        }

        class Empty(position: Point) : Cell(position) {
            override fun toString() = "."
        }

        sealed class Box(position: Point) : Cell(position) {
            abstract fun corresponding(map: Grid<Cell>): Box
            override fun toString() = "O"

            class Small(position: Point) : Box(position) {
                override fun corresponding(map: Grid<Cell>): Box {
                    TODO("Not yet implemented")
                }
            }

            class Left(position: Point) : Box(position) {
                override fun corresponding(map: Grid<Cell>): Right {
                    val rightPosition = position + Point.RIGHT
                    return map[rightPosition].let {
                        if (it is Right) {
                            it
                        } else {
                            error("No corresponding right box found")
                        }
                    }
                }

                override fun toString() = "["
            }

            class Right(position: Point) : Box(position) {
                override fun corresponding(map: Grid<Cell>): Left {
                    val leftPosition = position + Point.LEFT
                    return map[leftPosition].let {
                        if (it is Left) {
                            it
                        } else {
                            error("No corresponding left box found")
                        }
                    }
                }

                override fun toString() = "]"
            }
        }

        class Robot(position: Point) : Cell(position) {
            override fun toString() = "@"
        }
    }

    private fun findRobot(map: Grid<Cell>) =
        map.first { it.third is Cell.Robot }.third as Cell.Robot

    private fun canMoveBoxVertical(map: Grid<Cell>, cell: Cell, direction: Point): Boolean {
        val newPosition = cell.position + direction

        if (newPosition !in map.bounds) {
            return false
        }

        when (val newCell = map[newPosition]) {
            // A wall prevents movement
            is Cell.Wall -> {
                return false
            }
            // A small box can be moved easily
            is Cell.Box.Small -> {
                return canMoveBoxVertical(map, newCell, direction)
            }

            // A big box can be pushed and needs to check, whether it can be pushed
            is Cell.Box -> {
                // If the cell is a box, we need to check whether both parts can be moved
                if (direction == Point.UP || direction == Point.DOWN) {
                    val correspondingBox = newCell.corresponding(map)
                    return canMoveBoxVertical(map, newCell, direction)
                            && canMoveBoxVertical(map, correspondingBox, direction)
                }
            }
            // We can move to an empty cell
            is Cell.Empty -> {
                return true
            }

            is Cell.Robot -> throw IllegalStateException("Robot cannot be moved by itself")
        }
        return false
    }

    private fun moveCell(map: Grid<Cell>, cell: Cell, direction: Point): Boolean {
        val newPosition = cell.position + direction

        if (newPosition !in map.bounds) {
            return false
        }

        when (val newCell = map[newPosition]) {
            // A wall prevents movement
            is Cell.Wall -> {
                return false
            }
            // A small box can be moved easily
            is Cell.Box.Small -> {
                val canMove = moveCell(map, newCell, direction)
                if (canMove) {
                    moveCellDirect(map, cell, newPosition)
                }
                return canMove
            }
            // A box can be pushed and needs to check, whether it can be pushed
            is Cell.Box -> {
                when (direction) {
                    Point.LEFT, Point.RIGHT -> {
                        // When moving left or right, we can easily move recursively and
                        // check whether the box has been moved
                        val canMove = moveCell(map, newCell, direction)
                        if (canMove) {
                            moveCellDirect(map, cell, newPosition)
                        }
                        return canMove
                    }

                    Point.UP, Point.DOWN -> {
                        // When moving up or down, we need to check whether both parts can be moved
                        // Only then we can move the box as a whole
                        val correspondingBox = newCell.corresponding(map)
                        val canMove =
                            canMoveBoxVertical(map, newCell, direction)
                                    && canMoveBoxVertical(map, correspondingBox, direction)
                        if (canMove) {
                            // Moving up or down requires both parts to be moved
                            // We already concluded that both parts can be moved
                            moveCell(map, newCell, direction)
                            moveCell(map, correspondingBox, direction)

                            moveCellDirect(map, cell, newPosition)
                        }
                        return canMove
                    }
                }
            }

            is Cell.Empty -> {
                moveCellDirect(map, cell, newPosition)
                return true
            }

            is Cell.Robot -> throw IllegalStateException("Robot cannot be moved by itself")
        }
        return false
    }

    private fun moveCellDirect(map: Grid<Cell>, cell: Cell, newPosition: Point) {
        // Update the map
        map[cell.position] = Cell.Empty(cell.position)
        map[newPosition] = cell.apply { position = newPosition }
    }

    private fun simulateRobot(map: Grid<Cell>, moves: String) {
        var robot = findRobot(map)
        val movementDirections = moves.replace("\n", "").map { directions[it]!! }

        for (direction in movementDirections) {
            moveCell(map, robot, direction)
            robot = findRobot(map)
        }
    }

    override fun part1(inputFile: String): String {
        val (inputMap, moves) = readResourceTwoParts(inputFile)

        val map = inputMap.lines().mapIndexed { y, row ->
            row.mapIndexed { x, cell ->
                when (cell) {
                    '#' -> Cell.Wall(Point(x.toLong(), y.toLong()))
                    '.' -> Cell.Empty(Point(x.toLong(), y.toLong()))
                    'O' -> Cell.Box.Small(Point(x.toLong(), y.toLong()))
                    '@' -> Cell.Robot(Point(x.toLong(), y.toLong()))
                    else -> error("Unknown cell type: $cell")
                }
            }
        }.toGrid()

        simulateRobot(map, moves)

        val boxesSum = map.sumOf {
            if (it.third is Cell.Box) {
                it.third.position.x + it.third.position.y * 100
            } else {
                0
            }
        }

        return boxesSum.toString()
    }

    override fun part2(inputFile: String): String {
        val (inputMap, moves) = readResourceTwoParts(inputFile)

        val map = inputMap.lines().mapIndexed { y, row ->
            row.mapIndexed { x, cell ->
                val point1 = Point((x * 2), y)
                val point2 = Point((x * 2 + 1), y)
                when (cell) {
                    '#' -> listOf(Cell.Wall(point1), Cell.Wall(point2))
                    '.' -> listOf(Cell.Empty(point1), Cell.Empty(point2))
                    'O' -> listOf(Cell.Box.Left(point1), Cell.Box.Right(point2))
                    '@' -> listOf(Cell.Robot(point1), Cell.Empty(point2))
                    else -> error("Unknown cell type: $cell")
                }
            }.flatten()
        }.toGrid()

        simulateRobot(map, moves)

        // The solution is the same as in part 1, we just need to look at the left half of each box
        val boxesSum = map.sumOf {
            if (it.third is Cell.Box.Left) {
                it.third.position.x + it.third.position.y * 100
            } else {
                0
            }
        }

        return boxesSum.toString()
    }

    companion object {
        private val directions = mapOf(
            '^' to Point.UP,
            '<' to Point.LEFT,
            'v' to Point.DOWN,
            '>' to Point.RIGHT
        )
    }
}