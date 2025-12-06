package year2025

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResource

@AutoService(AOCSolution::class)
class Day02 : AOCSolution {
    override val year = 2025
    override val day = 2

    override fun part1(inputFile: String): String {
        var answer = 0L
        val digits = StringBuilder(10)

        readResource(inputFile).trim().split(RANGE_SEPARATOR).forEach { range ->
            val (lowerBound, upperBound) = range.split(BOUNDS_SEPARATOR, limit = 2).map(String::toLong)
            for (number in lowerBound..upperBound) {

                digits.clear().append(number)
                // Restrict to even numbers
                if (digits.length % 2 == 0) {
                    val middle = digits.length / 2
                    // Check if first half matches second half
                    if (digits.regionMatches(0, digits, middle, middle, false)) {
                        answer += number
                    }
                }
            }
        }
        return answer.toString()
    }

    override fun part2(inputFile: String): String {
        var answer = 0L
        val digits = StringBuilder(10)
        val buffer = StringBuilder(10)

        readResource(inputFile).trim().split(RANGE_SEPARATOR).forEach { range ->
            val (lowerBound, upperBound) = range.split(BOUNDS_SEPARATOR, limit = 2).map(String::toLong)

            for (number in lowerBound..upperBound) {
                digits.clear().append(number)
                // Check for all factors if the number is made up of repeated sequences
                for (factor in FACTORS[digits.length]) {
                    val repetitions = digits.length / factor
                    // Build the repeated sequence in the buffer
                    buffer.clear().repeat(digits.take(factor), repetitions)

                    // Check if the built buffer matches the original digits
                    if (buffer.compareTo(digits) == 0) {
                        answer += number
                        break
                    }
                }
            }
        }

        return answer.toString()
    }

    companion object {
        private const val RANGE_SEPARATOR = ','
        private const val BOUNDS_SEPARATOR = '-'

        /**
         * Precomputed prime factors for lengths 0 to 10.
         * These factors do not include the length itself, as invalid IDs cannot be made up of a single repetition.
         */
        private val FACTORS = arrayOf(
            intArrayOf(),
            intArrayOf(),
            intArrayOf(1),
            intArrayOf(1),
            intArrayOf(1, 2),
            intArrayOf(1),
            intArrayOf(1, 2, 3),
            intArrayOf(1),
            intArrayOf(1, 2, 4),
            intArrayOf(1, 3),
            intArrayOf(1, 2, 5)
        )
    }
}
