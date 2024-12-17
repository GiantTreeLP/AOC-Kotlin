package day8

import common.Point
import common.readResourceLines

private object Part1 {
    data class Antenna(val frequency: Char, val location: Point)
}

fun main() {
    val input = readResourceLines("day8/input")

    val antennas = input.mapIndexed { y, line ->
        line.mapIndexedNotNull { x, ch ->
            if (ch.isLetterOrDigit()) {
                Part1.Antenna(ch, Point(x, y))
            } else {
                null
            }
        }
    }
        .flatten()
        .groupBy(Part1.Antenna::frequency)

    val antennaVectorPairs = antennas.map { (_, frequencyAntennas) ->
        frequencyAntennas.map { antenna ->
            frequencyAntennas.mapNotNull { otherAntenna ->
                if (antenna != otherAntenna) {
                    antenna to (otherAntenna.location - antenna.location)
                } else {
                    null
                }
            }
        }
    }
        .flatten()
        .flatten()

    val antiNodes = antennaVectorPairs
        .map { (antenna, vector) ->
            antenna.location + (vector * 2)
        }.filter { (x, y) ->
            // Make sure the anti-node is within the bounds of the grid
            y in input.indices && x in input[0].indices
        }
        .distinct()

    print(antiNodes.size)
}
