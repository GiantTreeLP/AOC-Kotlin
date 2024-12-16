package day16

import common.Direction
import common.Grid.Companion.toGrid
import common.Point
import common.Position
import common.readResourceLines
import java.util.*


private object Part1 {
    sealed class Cell(
        val position: Point
    ) {

        override fun toString(): String {
            return "Cell(position=$position)"
        }

        class Wall(position: Point) : Cell(position)
        class Empty(position: Point) : Cell(position)
        class Start(position: Point) : Cell(position)
        class End(position: Point) : Cell(position)
    }
}

fun main() {
    val input = readResourceLines("day16/input")

    val maze = input.mapIndexed { y, row ->
        row.mapIndexed { x, c ->
            when (c) {
                '#' -> Part1.Cell.Wall(Point(x, y))
                '.' -> Part1.Cell.Empty(Point(x, y))
                'S' -> Part1.Cell.Start(Point(x, y))
                'E' -> Part1.Cell.End(Point(x, y))
                else -> error("Unknown cell: $c")
            }
        }
    }.toGrid()

    // Find the start and end cells
    val start = maze.find { it.third is Part1.Cell.Start }?.third ?: error("Start not found")
    val end = maze.find { it.third is Part1.Cell.End }?.third ?: error("End not found")

    val queue = PriorityQueue<Pair<Long, Pair<Position, Direction>>>(Comparator.comparingLong { it.first })
    val visited = mutableMapOf<Pair<Position, Direction>, Long>()

    queue.add(0L to (start.position to Direction.RIGHT))

    // Dijkstra's algorithm
    // Find all possible paths and their costs
    // This algorithm practically runs in three dimensions
    while (queue.isNotEmpty()) {
        val (cost, pair) = queue.poll()
        val (position, direction) = pair

        if (position == end.position) {
            println(cost)
            break
        }

        val left = direction.turnLeft()
        val right = direction.turnRight()
        val next = listOf(
            Triple(position + direction, direction, cost + 1L),
            Triple(position, left, cost + 1000L),
            Triple(position, right, cost + 1000L)
        )

        next.forEach { (nextPosition, nextDirection, nextCost) ->
            if (maze[nextPosition] !is Part1.Cell.Wall) {
                val key = nextPosition to nextDirection
                val currentCost = visited.getOrDefault(key, Long.MAX_VALUE)

                if (currentCost > nextCost) {
                    queue.add(nextCost to key)
                    visited[key] = nextCost
                }
            }
        }
    }
}