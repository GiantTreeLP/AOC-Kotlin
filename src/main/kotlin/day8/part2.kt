package day8

import common.readResourceLines

private object Part2 {
    data class Point2d(val x: Int, val y: Int) {
        operator fun plus(other: Point2d) = Point2d(x + other.x, y + other.y)
        operator fun minus(other: Point2d) = Point2d(x - other.x, y - other.y)

        fun translate(vector: Vector2d) = Point2d(x + vector.x, y + vector.y)

    }

    data class Vector2d(val x: Int, val y: Int) {
        operator fun times(scalar: Int) = Vector2d(x * scalar, y * scalar)
        operator fun plus(other: Vector2d) = Vector2d(x + other.x, y + other.y)

        operator fun minus(other: Vector2d) = Vector2d(x - other.x, y - other.y)

        companion object {
            fun fromPoints(start: Point2d, end: Point2d) = Vector2d(end.x - start.x, end.y - start.y)
        }

    }

    data class Antenna(val frequency: Char, val location: Point2d)
}

fun main() {
    val input = readResourceLines("day8/input")

    val antennas = input.mapIndexed { y, line ->
        line.mapIndexedNotNull { x, ch ->
            if (ch.isLetterOrDigit()) {
                Part2.Antenna(ch, Part2.Point2d(x, y))
            } else {
                null
            }
        }
    }
        .flatten()
        .groupBy(Part2.Antenna::frequency)

    val antennaVectorPairs = antennas.map { (frequency, frequencyAntennas) ->
        frequencyAntennas.map { antenna ->
            frequencyAntennas.mapNotNull { otherAntenna ->
                if (antenna != otherAntenna) {
                    antenna to Part2.Vector2d.fromPoints(antenna.location, otherAntenna.location)
                } else {
                    null
                }
            }
        }
    }.flatten().flatten()

    val antiNodes = antennaVectorPairs.map { (antenna, vector) ->
        buildList {
            var location = antenna.location.translate(vector)
            while (location.y in input.indices && location.x in input[0].indices) {
                add(location)
                location = location.translate(vector)
            }
        }
    }
        .flatten()
        .distinct()

    print(antiNodes.size)
}
