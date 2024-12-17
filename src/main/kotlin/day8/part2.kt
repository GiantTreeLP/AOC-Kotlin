package day8

import common.Point
import common.readResourceLines

private object Part2 {
    data class Antenna(val frequency: Char, val location: Point)
}

fun main() {
    val input = readResourceLines("day8/input")

    val antennas = input.mapIndexed { y, line ->
        line.mapIndexedNotNull { x, ch ->
            if (ch.isLetterOrDigit()) {
                Part2.Antenna(ch, Point(x, y))
            } else {
                null
            }
        }
    }
        .flatten()
        .groupBy(Part2.Antenna::frequency)

    val antennaVectorPairs = antennas
        .map { (_, frequencyAntennas) ->
            frequencyAntennas.map { antenna ->
                frequencyAntennas.mapNotNull { otherAntenna ->
                    if (antenna != otherAntenna) {
                        antenna to otherAntenna.location - antenna.location
                    } else {
                        null
                    }
                }
            }
        }.flatten().flatten()

    val antiNodes = antennaVectorPairs
        .map { (antenna, vector) ->
            buildList {
                var location = antenna.location + vector
                while (location.y in input.indices && location.x in input[0].indices) {
                    add(location)
                    location += vector
                }
            }
        }
        .flatten()
        .distinct()

    print(antiNodes.size)
}
