package day18

import common.Grid
import common.Point
import common.Position
import common.readResourceLines
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set


private object Part1 {
    const val WIDTH = 71
    const val HEIGHT = 71

    sealed class Cell(val position: Position) {
        class Wall(position: Position) : Cell(position)
        class Empty(position: Position) : Cell(position)
    }

}

fun main() {
    val input = readResourceLines("day18/input")
    val coordinates = input.map {
        val (x, y) = it.split(",").map(String::toInt)
        x to y
    }.take(1024)

    val memorySpace = Grid(Part1.WIDTH, Part1.HEIGHT) { x, y ->
        if (coordinates.contains(x to y)) {
            Part1.Cell.Wall(Point(x, y))
        } else {
            Part1.Cell.Empty(Point(x, y))
        }
    }

    // Dijkstra's algorithm for the shortest path from 0,0 to 70,70
    // Might be overcomplicated for this problem
    val start = memorySpace[0, 0]
    val end = memorySpace[70, 70]
    val visited = mutableMapOf<Pair<Position, Position>, Long>()
    val queue = PriorityQueue<Triple<Position, Position, Long>>(Comparator.comparingLong { it.third })
    queue.addAll(start.position.neighbours().mapNotNull {
        memorySpace.getOrNull(it)?.let { neighbour ->
            Triple(start.position, neighbour.position, 1L)
        }
    })

    while (queue.isNotEmpty()) {
        val (from, to, cost) = queue.poll()

        if (to == end.position) {
            visited[to to to] = cost
            println(cost)
            break
        }

        if (visited.getOrDefault(from to to, Long.MAX_VALUE) <= cost) {
            continue
        }

        visited[from to to] = cost

        queue.addAll(to.neighbours().mapNotNull {
            memorySpace.getOrNull(it)?.let { neighbour ->
                if (neighbour is Part1.Cell.Empty) {
                    Triple(to, neighbour.position, cost + 1)
                } else {
                    return@let null
                }
            }
        })
    }
}