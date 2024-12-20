package day20

import common.Direction
import common.Grid
import common.Grid.Companion.toGrid
import common.Position
import common.readResourceLines
import java.util.*

private object Part1 {
    sealed class Cell(val position: Position) {
        override fun toString() = "${this::class.simpleName}($position)"

        class Wall(position: Position) : Cell(position)
        sealed class Track(position: Position) : Cell(position) {
            class Empty(position: Position) : Track(position)
            class Start(position: Position) : Track(position)
            class End(position: Position) : Track(position)
        }
    }

    data class Cheat(val first: Position)

    fun findShortestPath(track: Grid<Cell>): Long {
        // Find start and end
        val start = track.first { it.third is Cell.Track.Start }.third
        val end = track.first { it.third is Cell.Track.End }.third

        // Find the shortest path using Dijkstra's algorithm
        val queue = PriorityQueue<Pair<Long, Pair<Position, Position>>>(Comparator.comparingLong { it.first })
        val visited = mutableMapOf<Pair<Position, Position>, Long>()

        Direction.ALL
            .map { start.position + it }
            .filter { track[it] is Cell.Track }
            .forEach {
                queue.add(1L to (start.position to it))
            }

        while (queue.isNotEmpty()) {
            val (cost, pair) = queue.poll()
            val (_, to) = pair

            if (to == end.position) {
                return cost
            }

            Direction.ALL.map { to + it }.filter { track.getOrNull(it) is Cell.Track }.forEach {
                val key = to to it
                val currentCost = visited.getOrDefault(key, Long.MAX_VALUE)

                if (cost + 1 < currentCost) {
                    queue.add(cost + 1 to (to to it))
                    visited[key] = cost + 1
                }
            }
        }

        error("No path found")
    }
}

fun main() {
    val input = readResourceLines("day20/input")

    val track = input.mapIndexed { y, row ->
        row.mapIndexed { x, cell ->
            when (cell) {
                '#' -> Part1.Cell.Wall(Position(x, y))
                '.' -> Part1.Cell.Track.Empty(Position(x, y))
                'S' -> Part1.Cell.Track.Start(Position(x, y))
                'E' -> Part1.Cell.Track.End(Position(x, y))
                else -> error("Unknown cell: $cell")
            }
        }
    }.toGrid()

    val reference = Part1.findShortestPath(track)

    println("Reference: $reference")

    // Cheats:
    val cheats = mutableMapOf<Long, Set<Part1.Cheat>>()

    // Horizontal
    for (y in 0 until track.height) {
        for (x in 0 until track.width - 1) {
            val firstPosition = Position(x, y)

            // Skip non-wall cells
            if (track[firstPosition] !is Part1.Cell.Wall) {
                continue
            }

            // Skip walls with no neighbouring track
            if (Direction.ALL.none { track.getOrNull(firstPosition + it) is Part1.Cell.Track }) {
                continue
            }

            val cheatedTrack = track.copy()
            cheatedTrack[firstPosition] = Part1.Cell.Track.Empty(firstPosition)

            val cheated = Part1.findShortestPath(cheatedTrack)
            cheats.compute(cheated) { _, list ->
                (list ?: emptySet()) + Part1.Cheat(firstPosition)
            }
        }
    }

    // Vertical
    for (x in 0 until track.width) {
        for (y in 0 until track.height - 1) {
            val firstPosition = Position(x, y)

            // Skip non-wall cells
            if (track[firstPosition] !is Part1.Cell.Wall) {
                continue
            }

            // Skip walls with no neighbouring track
            if (Direction.ALL.none { track.getOrNull(firstPosition + it) is Part1.Cell.Track }) {
                continue
            }

            val cheatedTrack = track.copy()
            cheatedTrack[firstPosition] = Part1.Cell.Track.Empty(firstPosition)

            val cheated = Part1.findShortestPath(cheatedTrack)
            cheats.compute(cheated) { _, list ->
                (list ?: emptySet()) + Part1.Cheat(firstPosition)
            }
        }
    }

    val saved100 = cheats
        .toSortedMap()
        .mapKeys {
            reference - it.key
        }
        .mapValues {
            it.value.size
        }
        .entries
        .filter { it.key >= 100 }
        .sumOf { it.value }

    println("Saved 100 steps or more: $saved100")
}