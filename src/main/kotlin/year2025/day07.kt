package year2025

import com.google.auto.service.AutoService
import common.*
import common.grid.mapGrid
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
                val cell = diagram[x, y - 1]
                if (cell == Cell.Splitter || cell == Cell.Start) {
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

        val splitterDiagram = diagram.mapGrid {
            when (it) {
                Cell.Splitter, Cell.Start -> {
                    Splitter()
                }

                else -> emptySplitter
            }
        }

        // Working stack of beam origin and split origin positions
        val stack = ArrayDeque<Pair<Position, Position>>()
        stack.add(startPosition to startPosition)

        // Splitter positions mapped to the count of timelines to them
        // Start with the start position and 1 timeline.
        splitterDiagram[startPosition].apply {
            incomingBeams = 1
            hasSpawned = true
        }

        // Count the timelines per diagram exit, which is the bottom-most row
        val diagramExits = LongArray(diagram.width)

        while (stack.isNotEmpty()) {
            // Breadth first search for memorizing the amount of paths to a splitter
            val (beamOrigin, splitOrigin) = stack.poll()
            val originPathCount = splitterDiagram[splitOrigin].incomingBeams

            val nextPosition = beamOrigin + Direction.DOWN

            if (nextPosition.y < height) {
                if (diagram[nextPosition] == Cell.Splitter) {
                    val splitter = splitterDiagram[nextPosition]
                    if (!splitter.hasSpawned) {
                        // Only spawn new beams, if they weren't spawned already
                        stack.add((nextPosition + Direction.LEFT) to nextPosition)
                        stack.add((nextPosition + Direction.RIGHT) to nextPosition)
                        splitter.hasSpawned = true
                        // Initialize the count
                        splitter.incomingBeams = originPathCount
                    } else {
                        splitter.incomingBeams += originPathCount
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
        data class Splitter(var incomingBeams: Long = 0L, var hasSpawned: Boolean = false)

        val emptySplitter = Splitter()

        enum class Cell(val char: Char) {
            Start('S'),
            Empty('.'),
            Splitter('^'),
            Beam('|');

            override fun toString(): String {
                return char.toString()
            }

            companion object {
                fun byChar(char: Char) = when (char) {
                    'S' -> Start
                    '.' -> Empty
                    '^' -> Splitter
                    '|' -> Beam

                    else -> throw NoSuchElementException()
                }

            }
        }
    }
}
