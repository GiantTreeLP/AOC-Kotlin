package day16

import common.Direction
import common.Grid.Companion.toGrid
import common.Point
import common.Position
import common.readResourceLines
import java.util.*
import kotlin.math.min

private object Part2 {
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
                '#' -> Part2.Cell.Wall(Point(x, y))
                '.' -> Part2.Cell.Empty(Point(x, y))
                'S' -> Part2.Cell.Start(Point(x, y))
                'E' -> Part2.Cell.End(Point(x, y))
                else -> error("Unknown cell: $c")
            }
        }
    }.toGrid()

    // Find the start and end cells
    val start = maze.find { it.third is Part2.Cell.Start }?.third ?: error("Start not found")
    val end = maze.find { it.third is Part2.Cell.End }?.third ?: error("End not found")

    val queue = PriorityQueue<Pair<Long, Pair<Position, Direction>>>(Comparator.comparingLong { it.first })
    val visited = mutableMapOf<Pair<Position, Direction>, Long>()
    var lowestCost = Long.MAX_VALUE

    queue.add(0L to (start.position to Direction.RIGHT))

    // Dijkstra's algorithm
    // Find all possible paths and their costs
    // This algorithm practically runs in three dimensions
    // Unlike in part 1, we continue to find all paths with the lowest cost
    while (queue.isNotEmpty()) {
        val (cost, pair) = queue.poll()
        val (position, direction) = pair

        if (position == end.position) {
            lowestCost = min(lowestCost, cost)
            continue
        }

        val left = direction.turnLeft()
        val right = direction.turnRight()
        val next = listOf(
            Triple(position + direction, direction, cost + 1L),
            Triple(position, left, cost + 1000L),
            Triple(position, right, cost + 1000L)
        )

        next.forEach { (nextPosition, nextDirection, nextCost) ->
            if (maze[nextPosition] !is Part2.Cell.Wall) {
                val key = nextPosition to nextDirection
                val currentCost = visited.getOrDefault(key, Long.MAX_VALUE)

                if (currentCost > nextCost) {
                    queue.add(nextCost to key)
                    visited[key] = nextCost
                }
            }
        }
    }

    // Run backwards to collect all the paths with the lowest cost
    val stack = ArrayDeque<Triple<Position, Direction, Long>>()
    val path = mutableSetOf<Position>()

    Position.ALL.forEach { direction ->
        visited[end.position to direction]?.let { cost ->
            if (cost == lowestCost) {
                stack.addLast(Triple(end.position, direction, cost))
            }
        }
    }

    while (stack.isNotEmpty()) {
        val (position, direction, cost) = stack.pollFirst()
        path.add(position)

        if (position == start.position) {
            continue
        }

        val left = direction.turnLeft()
        val right = direction.turnRight()
        val next = listOf(
            Triple(position - direction, direction, cost - 1L),
            Triple(position, left, cost - 1000L),
            Triple(position, right, cost - 1000L)
        )

        next.forEach { (nextPosition, nextDirection, nextCost) ->
            val key = nextPosition to nextDirection

            if (visited[key] == nextCost) {
                stack.addLast(Triple(nextPosition, nextDirection, nextCost))
            }
        }
    }

    println(path.size)
}