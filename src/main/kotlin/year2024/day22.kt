package year2024

import com.google.auto.service.AutoService
import common.*
import java.util.*


@AutoService(AOCSolution::class)
class Day22 : AOCSolution {
    override val year = 2024
    override val day = 22

    private fun nextSecretNumber(start: Long): Long {
        // Modulo 2^24 is the same as "and" with 2^24 - 1
        val pruneMask = 16777216L - 1L
        // * 64 is the same as shifting left by 6
        val mul64 = ((start shl 6) xor start) and pruneMask
        // / 32 is the same as shifting right by 5
        val div32 = ((mul64 shr 5) xor mul64) and pruneMask
        // * 2048 is the same as shifting left by 11
        val mul2048 = ((div32 shl 11) xor div32) and pruneMask
        return mul2048
    }

    override fun part1(inputFile: String): String {
        val secretNumbers = readResourceLines(inputFile)
            .map { it.toLong() }
            .toLongArray()

        repeat(NUMBERS_PER_DAY) {
            for (i in secretNumbers.indices) {
                secretNumbers[i] = nextSecretNumber(secretNumbers[i])
            }
        }

        return secretNumbers.sum().toString()
    }

    override fun part2(inputFile: String): String {
        // There is a different sample input for part 2
        val input = if (inputFile.endsWith("sample")) {
            readResourceLines(inputFile + "2")
        } else {
            readResourceLines(inputFile)
        }
        val buyers = input
            .map {
                LongArray(NUMBERS_PER_DAY + 1).apply {
                    this[0] = it.toLong()
                    for (i in 1..NUMBERS_PER_DAY) {
                        this[i] = nextSecretNumber(this[i - 1])
                    }
                }
            }

        // Calculate the prices and price differences for each buyer.
        // The pairs are the price (the ones digit) and the key/unique value of each sequence of differences
        val differences = buyers
            .map { secretNumbers ->
                // Get the ones digit
                val prices = secretNumbers.mapToIntArray {
                    it.toInt() % 10
                }

                // Get the differences between each number
                val differenceKeys = prices
                    .zipWithNext { a, b -> (b - a) }
                    // Transform the differences to a singular unique value (integer)
                    .mapWindowed(4) { sequence, from, _ ->
                        // Bring each byte from -9 to 9 to 0 to 18, multiply by 19^i and sum
                        // This generates a unique value for each sequence of 4 differences
                        (sequence[from + 0] + 9) +
                                (sequence[from + 1] + 9) * 19 +
                                (sequence[from + 2] + 9) * 361 +
                                (sequence[from + 3] + 9) * 6859
                    }

                // Drop the first 4 prices, as they are not relevant (initial secret number price and 3 next prices)
                prices.dropFromArray(4) to differenceKeys
            }

        // Cache to hold the value/sum of each sequence of 4 differences
        val sequenceCache = IntArray(NUMBER_OF_SEQUENCES)
        val seenSequence = BooleanArray(NUMBER_OF_SEQUENCES)

        // Go through each sequence of differences
        // and get their *first* prices of each sequence.
        // Sum them in the cache.
        for ((prices, priceDifferences) in differences) {
            // Reset the "seen" array
            Arrays.fill(seenSequence, false)
            for (index in priceDifferences.indices) {
                val key = priceDifferences[index]
                if (!seenSequence[key]) {
                    sequenceCache[key] += prices[index]
                    seenSequence[key] = true
                }
            }
        }

        return sequenceCache.max().toString()
    }

    companion object {
        private const val NUMBERS_PER_DAY = 2000

        // 19^4, the differences range from -9 to 9 and the sequences are 4 numbers long
        private const val NUMBER_OF_SEQUENCES = 19 * 19 * 19 * 19
    }
}
