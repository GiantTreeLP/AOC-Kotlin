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
        return readResourceBinary(inputFile).lineSequence().sumOf { line ->
            val indices = IntArray(2) { -1 }
            findSolution(indices, line)
            indices.sort()

            indices.fold(0L) { acc, i -> acc * 10L + (line[i].toDigit()) }
        }.toString()
    }

    override fun part2(inputFile: String): String {
        return readResourceBinary(inputFile).lineSequence().sumOf { line ->
            val indices = IntArray(12) { -1 }
            findSolution(indices, line)
            indices.sort()

            indices.fold(0L) { acc, i -> acc * 10L + (line[i].toDigit()) }
        }.toString()
    }

    private fun findSolution(
        indices: IntArray,
        buffer: EightBitString,
        currentDepth: Int = 0,
        startIndex: Int = 0,
        endIndex: Int = buffer.length,
    ): Int {
        if (currentDepth >= indices.size) {
            // We have reached the depth limit
            return currentDepth
        }
        if (startIndex == endIndex) {
            // We have encountered the end of the searchable range
            return currentDepth
        }

        val maxIndex = buffer.subSequence(startIndex, endIndex).indexOfMax() + startIndex
        indices[currentDepth] = maxIndex

        // Try to find a solution to the right of the maximum digit
        var depth = findSolution(
            indices,
            buffer,
            currentDepth + 1,
            maxIndex + 1,
            endIndex,
        )
        // Check whether all digits are found
        if (depth <= indices.lastIndex) {
            // Try to find the remaining digits to the left of the current remaining digit
            depth = findSolution(
                indices,
                buffer,
                depth,
                startIndex,
                maxIndex
            )
        }
        return depth
    }

    companion object {
        fun ByteArray.lineSequence(): Sequence<EightBitString> {
            val buffer = EightBitString(this)
            var currentIndex = 0
            val newLine = '\n'
            return generateSequence {
                var b: EightBitString? = null
                for (i in currentIndex until buffer.limit()) {
                    if (buffer[i] == newLine) {
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
                currentIndex += b.length + 1
                return@generateSequence b
            }
        }

        fun EightBitString.indexOfMax(): Int {
            if (remaining() == 0) {
                return -1
            }
            var maxIndex = 0
            var max = getByte(maxIndex)
            for (i in position() until this.limit()) {
                val c = getByte(i)
                if (c > max) {
                    maxIndex = i
                    max = c
                }
            }
            return maxIndex
        }

        fun Char.toDigit(): Long = (this - '0'.code).code.toLong()
    }
}
