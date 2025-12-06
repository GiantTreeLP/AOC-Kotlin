package year2025

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceLines
import kotlin.math.absoluteValue

@AutoService(AOCSolution::class)
class Day01 : AOCSolution {
    override val year = 2025
    override val day = 1

    override fun part1(inputFile: String): String {
        var safePosition = SAFE_START
        var password = 0
        readResourceLines(inputFile).forEach { line ->
            val direction = line[0]
            val distance = line.substring(1).toInt()
            when (direction) {
                DIRECTION_LEFT -> safePosition -= distance
                DIRECTION_RIGHT -> safePosition += distance
            }
            // The dial is circular
            safePosition %= SAFE_POSITIONS
            if (safePosition == 0) {
                // Count the times we end up at position 0
                password++
            }
        }

        return password.toString()
    }

    override fun part2(inputFile: String): String {
        var safePosition = SAFE_START
        var password = 0
        readResourceLines(inputFile).forEach { line ->
            val direction = line[0]
            val distance = line.substring(1).toInt()

            val oldPosition = safePosition

            when (direction) {
                DIRECTION_LEFT -> safePosition -= distance
                DIRECTION_RIGHT -> safePosition += distance
            }

            // Count how many times we cross position 0
            var crossings = 0
            // If the new position is on the left side and the old one on the right side, we crossed 0
            // Also count if we land exactly on 0
            if ((oldPosition > 0 && safePosition < 0) || safePosition == 0) {
                crossings++
            }
            // Add the number of full laps we made
            crossings += (safePosition / SAFE_POSITIONS).absoluteValue
            password += crossings
            // The dial is circular
            safePosition = safePosition modulo SAFE_POSITIONS
        }

        return password.toString()
    }

    companion object {
        const val SAFE_START = 50
        const val SAFE_POSITIONS = 100

        const val DIRECTION_LEFT = 'L'
        const val DIRECTION_RIGHT = 'R'

        private infix fun Int.modulo(other: Int): Int {
            val result = this % other
            return if (result < 0) result + other else result
        }
    }
}
