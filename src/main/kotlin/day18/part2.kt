package day18

import common.Grid
import common.Point
import common.Position
import common.readResourceLines
import day18.Part2.binarySearch
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set


private object Part2 {
    const val WIDTH = 71
    const val HEIGHT = 71

    sealed class Cell(val position: Position) {
        class Wall(position: Position) : Cell(position)
        class Empty(position: Position) : Cell(position)
    }

    fun solveMaze(cutOff: Int, coordinates: List<Point>): Long {
        val coordinatesSlice = coordinates.take(cutOff)
        val memorySpace = Grid(WIDTH, HEIGHT) { point ->
            if (coordinatesSlice.contains(point)) {
                Cell.Wall(point)
            } else {
                Cell.Empty(point)
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
                break
            }

            if (visited.getOrDefault(from to to, Long.MAX_VALUE) <= cost) {
                continue
            }

            visited[from to to] = cost

            queue.addAll(to.neighbours().mapNotNull {
                memorySpace.getOrNull(it)?.let { neighbour ->
                    if (neighbour is Cell.Empty) {
                        Triple(to, neighbour.position, cost + 1)
                    } else {
                        return@let null
                    }
                }
            })
        }

        val cost = visited[end.position to end.position]
        if (cost != null) {
            println("Path found for cut off $cutOff")
            return cost
        } else {
            println("No path found for cut off $cutOff")
            return -1L
        }
    }

    fun binarySearch(coordinates: List<Point>): Int {
        // Given that the maze is getting more and more blocked, we can use binary search
        // to find the cut-off point where a path is no longer possible
        var low = 0
        var high = coordinates.size
        while (low != high) {
            val mid = (low + high) / 2
            val cost = solveMaze(mid, coordinates)
            if (cost >= 0L) {
                low = mid + 1
            } else {
                high = mid
            }
        }
        return low
    }

}

fun main() {
    val input = readResourceLines("day18/input")

    val coordinates = input.map {
        val (x, y) = it.split(",").map(String::toInt)
        Point(x, y)
    }

    val byteIndex = binarySearch(coordinates) - 1
    println(byteIndex)
    val point = coordinates[byteIndex]
    println("${point.x},${point.y}")
}