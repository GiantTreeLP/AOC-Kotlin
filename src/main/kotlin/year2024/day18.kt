package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.Point
import common.Position
import common.grid.DefaultGrid
import common.readResourceLines
import java.util.*

@AutoService(AOCSolution::class)
class Day18 : AOCSolution {
    override val year = 2024
    override val day = 18

    private sealed class Cell(val position: Position) {
        class Wall(position: Position) : Cell(position)
        class Empty(position: Position) : Cell(position)
    }

    private fun solveMaze(width: Int, height: Int, cutOff: Int, coordinates: List<Point>): Long {
        val coordinatesSlice = coordinates.take(cutOff)
        val memorySpace = DefaultGrid(width, height) { point ->
            if (coordinatesSlice.contains(point)) {
                Cell.Wall(point)
            } else {
                Cell.Empty(point)
            }
        }

        // Dijkstra's algorithm for the shortest path from 0,0 to the bottom right corner
        val start = memorySpace[0, 0]
        val end = memorySpace[width - 1, height - 1]
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

        return visited[end.position to end.position] ?: -1L
    }

    private fun binarySearch(width: Int, height: Int, coordinates: List<Point>): Int {
        // Given that the maze is getting more and more blocked, we can use binary search
        // to find the cut-off point where a path is no longer possible
        var low = 0
        var high = coordinates.size
        while (low != high) {
            val mid = (low + high) / 2
            val cost = solveMaze(width, height, mid, coordinates)
            if (cost >= 0L) {
                low = mid + 1
            } else {
                high = mid
            }
        }
        return low
    }


    override fun part1(inputFile: String): String {
        val input = readResourceLines(inputFile)

        val width = if (inputFile.endsWith("sample")) SAMPLE_WIDTH else WIDTH
        val height = if (inputFile.endsWith("sample")) SAMPLE_HEIGHT else HEIGHT
        val cutOff = if (inputFile.endsWith("sample")) PART1_SAMPLE_CUTOFF else PART1_CUTOFF

        val coordinates = input.map {
            val (x, y) = it.split(",").map(String::toInt)
            Point(x, y)
        }

        // Take the first 1024 coordinates (bytes)
        return solveMaze(width, height, cutOff, coordinates).toString()
    }

    override fun part2(inputFile: String): String {
        val input = readResourceLines(inputFile)

        val width = if (inputFile.endsWith("sample")) SAMPLE_WIDTH else WIDTH
        val height = if (inputFile.endsWith("sample")) SAMPLE_HEIGHT else HEIGHT

        // Take the first 1024 coordinates (bytes)
        val coordinates = input.map {
            val (x, y) = it.split(",").map(String::toInt)
            Point(x, y)
        }

        val byteIndex = binarySearch(width, height, coordinates) - 1
        val point = coordinates[byteIndex]
        return "${point.x},${point.y}"
    }

    companion object {
        private const val SAMPLE_WIDTH = 7
        private const val SAMPLE_HEIGHT = 7

        private const val WIDTH = 71
        private const val HEIGHT = 71

        private const val PART1_CUTOFF = 1024
        private const val PART1_SAMPLE_CUTOFF = 12
    }
}