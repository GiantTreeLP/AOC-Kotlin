package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.Point
import common.readResourceLines

@AutoService(AOCSolution::class)
class Day08 : AOCSolution {
    override val year = 2024
    override val day = 8

    private data class Antenna(val frequency: Char, val location: Point)

    private fun parseAntennas(inputFile: String): Pair<List<String>, Map<Char, List<Antenna>>> {
        val input = readResourceLines(inputFile)

        val antennas = input.mapIndexed { y, line ->
            line.mapIndexedNotNull { x, ch ->
                if (ch.isLetterOrDigit()) {
                    Antenna(ch, Point(x, y))
                } else {
                    null
                }
            }
        }
            .flatten()
            .groupBy(Antenna::frequency)
        return input to antennas
    }

    override fun part1(inputFile: String): String {
        val (input, antennas) = parseAntennas(inputFile)

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
            .toSet()

        return antiNodes.size.toString()
    }

    override fun part2(inputFile: String): String {
        val (input, antennas) = parseAntennas(inputFile)

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
            .toSet()

        return antiNodes.size.toString()
    }
}