package year2025

import com.google.auto.service.AutoService
import common.AOCSolution
import common.EightBitString
import common.readResourceBinary

@AutoService(AOCSolution::class)
class Day03 : AOCSolution {
    override val year = 2025
    override val day = 3

    override fun part1(inputFile: String): String {
        return readResourceBinary(inputFile).lineSequence().sumOf { batteryBank ->
            findHighestJoltage(batteryBank, 2)
        }.toString()
    }

    override fun part2(inputFile: String): String {
        return readResourceBinary(inputFile).lineSequence().sumOf { line ->
            findHighestJoltage(line, 12)
        }.toString()
    }

    private fun findHighestJoltage(
        bank: EightBitString,
        batteries: Int,
    ): Long {
        val digitsArray = ByteArray(batteries) { -1 }

        var lastDigitIndex = 0
        repeat(batteries) { currentDigit ->
            val remainingDigits = batteries - currentDigit
            val lastIndex = bank.length - remainingDigits + 1

            val maxIndex = bank.indexOfMax(lastDigitIndex, lastIndex)
            lastDigitIndex = maxIndex + 1
            digitsArray[batteries - remainingDigits] = bank[maxIndex].toDigit()
        }

        return digitsArray.fold(0L) { acc, i -> acc * 10L + i }
    }


    private companion object {
        private fun ByteArray.lineSequence(): Sequence<EightBitString> {
            val buffer = EightBitString(this)
            var currentIndex = 0
            return generateSequence {
                var b: EightBitString? = null
                for (i in currentIndex until buffer.limit()) {
                    if (buffer[i] == '\n') {
                        b = buffer.subSequence(currentIndex, i)
                        break
                    }
                }
                // If we could not find the separator, return the remaining buffer
                if (b == null) {
                    b = buffer.subSequence(currentIndex, buffer.limit())
                }

                if (b.isEmpty()) {
                    return@generateSequence null
                }
                @Suppress("AssignedValueIsNeverRead")
                currentIndex += b.length + 1
                return@generateSequence b
            }
        }

        private fun EightBitString.indexOfMax(
            startIndex: Int,
            endIndex: Int,
        ): Int {
            if (startIndex >= endIndex) {
                return -1
            }
            var maxIndex = startIndex
            var max = 0.toByte()
            for (i in startIndex until endIndex) {
                val c = getByte(i)
                if (c > max) {
                    maxIndex = i
                    max = c
                }
            }
            return maxIndex
        }

        private fun Char.toDigit(): Byte = (this - '0').toByte()
    }
}
