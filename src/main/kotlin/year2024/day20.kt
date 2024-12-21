package year2024

import com.google.auto.service.AutoService
import common.*
import common.Grid.Companion.toGrid
import kotlin.math.abs
import kotlin.math.min

@AutoService(AOCSolution::class)
class Day20 : AOCSolution {
    override val year = 2024
    override val day = 20

    private sealed class Cell(val position: Position) {
        override fun toString() = "${this::class.simpleName}($position)"

        class Wall(position: Position) : Cell(position)
        sealed class Track(position: Position) : Cell(position) {
            class Empty(position: Position) : Track(position)
            class Start(position: Position) : Track(position)
            class End(position: Position) : Track(position)
        }
    }

    /**
     * Calculates the distance from [start] to [end] for each cell in the [track].
     * Returns two grids:
     * - The first grid contains the distance from [start] to each cell
     * - The second grid contains the distance from each cell to [end]
     */
    private fun calculateDistances(track: Grid<Cell>, start: Cell, end: Cell): Pair<Grid<Int>, Grid<Int>> {
        val bounds = track.bounds
        val distanceToStart = Grid(track.width, track.height) { _, _ -> Int.MAX_VALUE }

        var currentCell = start
        var currentDistance = 0
        distanceToStart[currentCell.position] = 0

        while (currentCell != end) {
            for (direction in Direction.ALL) {
                val nextPosition = currentCell.position + direction
                if (nextPosition in bounds
                    && track[nextPosition] !is Cell.Wall
                    && distanceToStart[nextPosition] == Int.MAX_VALUE
                ) {
                    currentCell = track[nextPosition]
                    break
                }
            }
            currentDistance++
            distanceToStart[currentCell.position] = currentDistance
        }

        val totalDistance = distanceToStart[end.position]
        val distanceToEnd = Grid(track.width, track.height) { position ->
            val distance = distanceToStart[position]
            if (distance == Int.MAX_VALUE) {
                Int.MAX_VALUE
            } else {
                totalDistance - distance
            }
        }
        return distanceToStart to distanceToEnd
    }

    /**
     * Builds a list of vectors that can be used to cheat
     * All vectors have a manhattan distance of at most [maximumDistance].
     * The vectors cover the bottom right half of the manhattan distance diamond.
     * The vector (0, 0) is not included for obvious reasons.
     *
     * These vectors are enough to cover all possible cheats.
     */
    private fun getCheatVectors(maximumDistance: Int): List<Point> {
        return buildList {
            for (y in -maximumDistance..maximumDistance) {
                val start = if (y > 0) 0 else 1
                for (x in start..maximumDistance - abs(y)) {
                    add(Point(x, y))
                }
            }
        }
    }

    private fun calculateCheats(
        track: Grid<Cell>,
        distanceFieldStart: Grid<Int>,
        distanceFieldEnd: Grid<Int>,
        referenceDistance: Int,
        cheatVectors: List<Point>,
    ): Int {
        val bounds = track.bounds

        return track.sumOf { (_, _, cell) ->
            cheatVectors.count { vector ->
                val destination = cell.position + vector
                // Check if the destination is within bounds and not a wall
                // This makes sure our cheat vector is valid and doesn't end up in a wall
                if (destination in bounds &&
                    track[destination] !is Cell.Wall
                ) {

                    // Sum of:
                    // - The distance from start to one end of the vector
                    // - The distance from the other end of the vector to the end
                    // - The manhattan distance of the vector itself
                    val distance =
                        min(
                            distanceFieldStart[cell.position],
                            distanceFieldStart[destination]
                        ) + min(
                            distanceFieldEnd[cell.position],
                            distanceFieldEnd[destination]
                        ) + vector.manhattanDistance
                    referenceDistance - distance >= MINIMUM_SAVED_PICOSECONDS
                } else {
                    false
                }
            }
        }
    }

    private data class TrackAndCheatVectors(
        val track: Grid<Cell>,
        val start: Cell.Track.Start,
        val end: Cell.Track.End,
        val distanceFieldStart: Grid<Int>,
        val distanceFieldEnd: Grid<Int>,
        val cheatVectors: List<Point>
    )

    private fun parseTrackAndCreateCheatVectors(inputFile: String, maximumDistance: Int): TrackAndCheatVectors {
        val input = readResourceLines(inputFile)

        val track = input.mapIndexed { y, row ->
            row.mapIndexed { x, cell ->
                when (cell) {
                    '#' -> Cell.Wall(Position(x, y))
                    '.' -> Cell.Track.Empty(Position(x, y))
                    'S' -> Cell.Track.Start(Position(x, y))
                    'E' -> Cell.Track.End(Position(x, y))
                    else -> error("Unknown cell: $cell")
                }
            }
        }.toGrid()

        val start = track.values.first { it is Cell.Track.Start }
        val end = track.values.first { it is Cell.Track.End }

        val (distanceFieldStart, distanceFieldEnd) = calculateDistances(track, start, end)

        val cheatVectors = getCheatVectors(maximumDistance)

        return TrackAndCheatVectors(
            track,
            start as Cell.Track.Start,
            end as Cell.Track.End,
            distanceFieldStart,
            distanceFieldEnd,
            cheatVectors
        )
    }

    override fun part1(inputFile: String): String {
        val (track,
            _,
            end,
            distanceFieldStart,
            distanceFieldEnd,
            cheatVectors) = parseTrackAndCreateCheatVectors(inputFile, 2)

        val goodCheats = calculateCheats(
            track,
            distanceFieldStart,
            distanceFieldEnd,
            distanceFieldStart[end.position],
            cheatVectors
        )

        return goodCheats.toString()
    }

    override fun part2(inputFile: String): String {
        val (track,
            _,
            end,
            distanceFieldStart,
            distanceFieldEnd,
            cheatVectors) = parseTrackAndCreateCheatVectors(inputFile, 20)

        val goodCheats = calculateCheats(
            track,
            distanceFieldStart,
            distanceFieldEnd,
            distanceFieldStart[end.position],
            cheatVectors
        )

        return goodCheats.toString()
    }

    companion object {
        private const val MINIMUM_SAVED_PICOSECONDS = 100
    }
}