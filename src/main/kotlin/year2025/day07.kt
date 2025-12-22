package year2025

import com.google.auto.service.AutoService
import common.*
import common.grid.CharGrid.Companion.mapGrid
import common.grid.CharGrid.Companion.toCharGrid
import java.util.*

@AutoService(AOCSolution::class)
class Day07 : AOCSolution {
    override val year = 2025
    override val day = 7

    override fun part1(inputFile: String): String {
        val diagram = readResourceLines(inputFile)
            .mapArray { line -> line.toCharArray() }
            .toCharGrid()

        var count = 0
        for (y in 1 until diagram.height) {
            for (x in 0 until diagram.width) {
                // Search for beam sources in the preceding row
                val cell = diagram.getPrimitive(x, y - 1)
                if (cell == BEAM || cell == START) {
                    // Simulate the beam moving down
                    when (diagram.getPrimitive(x, y)) {
                        EMPTY -> diagram.setPrimitive(x, y, BEAM)
                        SPLITTER -> {
                            // Split the beam and count this splitter
                            diagram.setPrimitive(x - 1, y, BEAM)
                            diagram.setPrimitive(x + 1, y, BEAM)
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
            .mapArray { line -> line.toCharArray() }
            .toCharGrid()
        val height = diagram.height.toLong()

        val startPosition = diagram.positionOfFirst { it == START }

        val splitterDiagram = diagram.mapGrid {
            when (it) {
                SPLITTER, START -> {
                    SplitterCell()
                }

                else -> emptySplitterCell
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
                if (diagram.getPrimitive(nextPosition) == SPLITTER) {
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
        data class SplitterCell(var incomingBeams: Long = 0L, var hasSpawned: Boolean = false)

        val emptySplitterCell = SplitterCell()

        const val START = 'S'
        const val EMPTY = '.'
        const val SPLITTER = '^'
        const val BEAM = '|'
    }
}
