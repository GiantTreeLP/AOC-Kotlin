package year2025

import com.google.auto.service.AutoService
import common.*
import common.grid.positionOfFirst
import common.grid.toGrid
import java.util.*

@AutoService(AOCSolution::class)
class Day07 : AOCSolution {
    override val year = 2025
    override val day = 7

    override fun part1(inputFile: String): String {
        val diagram = readResourceLines(inputFile)
            .mapArray { line -> line.mapArray { char -> Cell.byChar(char) } }
            .toGrid()

        var count = 0
        for (y in 1 until diagram.height) {
            for (x in 0 until diagram.width) {
                // Search for beam sources in the preceding row
                if (diagram[x, y - 1] in Cell.beamSourceTypes) {
                    // Simulate the beam moving down
                    when (diagram[x, y]) {
                        Cell.Empty -> diagram[x, y] = Cell.Beam
                        Cell.Splitter -> {
                            // Split the beam and count this splitter
                            diagram[x - 1, y] = Cell.Beam
                            diagram[x + 1, y] = Cell.Beam
                            count++
                        }

                        else -> continue
                    }
                }
            }
        }

        return count.toString()
    }

    override fun part2(inputFile: String): String {
        val diagram = readResourceLines(inputFile)
            .mapArray { line -> line.mapArray { char -> Cell.byChar(char) } }
            .toGrid()
        val height = diagram.height.toLong()

        val startPosition = diagram.positionOfFirst { it == Cell.Start }

        // Working stack of beam origin and split origin positions
        val stack = ArrayDeque<Pair<Position, Position>>()
        stack.add(startPosition to startPosition)

        // Splitter positions mapped to the count of timelines to them
        // Start with the start position and 1 timeline.
        val splitters = mutableMapOf<Position, Long>(startPosition to 1)

        // Keep track of all splitters for which new beams have been spawned already
        // Could be used to solve part 1, as well
        val spawnedSplitters = mutableSetOf<Position>()

        // Count the timelines per diagram exit, which are all
        val diagramExits = LongArray(diagram.width)

        while (stack.isNotEmpty()) {
            // Breadth first search for memorizing the amount of paths to a splitter
            val (beamOrigin, splitOrigin) = stack.poll()
            val originPathCount = splitters.getValue(splitOrigin)

            val nextPosition = beamOrigin + Direction.DOWN

            if (nextPosition.y < height) {
                if (diagram[nextPosition] == Cell.Splitter) {
                    if (nextPosition !in spawnedSplitters) {
                        // Only spawn new beams, if they weren't spawned already
                        stack.add((nextPosition + Direction.LEFT) to nextPosition)
                        stack.add((nextPosition + Direction.RIGHT) to nextPosition)
                        spawnedSplitters.add(nextPosition)
                        // Initialize the count
                        splitters[nextPosition] = originPathCount
                    } else {
                        splitters.computeIfPresent(nextPosition) { _, v -> v + originPathCount }
                    }
                } else {
                    // Just move down
                    stack.add(nextPosition to splitOrigin)
                }
            } else {
                diagramExits[nextPosition.x.toInt()] += originPathCount
            }
        }

        // Sum the count of timelines leading to the bottom row, i.e. leaving the diagram for each position
        return diagramExits.sum().toString()
    }

    private companion object {
        enum class Cell(val char: Char) {
            Start('S'),
            Empty('.'),
            Splitter('^'),
            Beam('|');

            override fun toString(): String {
                return char.toString()
            }

            companion object {
                fun byChar(char: Char) = entries.first { it.char == char }

                val beamSourceTypes = arrayOf(Start, Beam)
            }
        }
    }
}
