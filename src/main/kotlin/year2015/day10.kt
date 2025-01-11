package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResource
import kotlin.math.pow

@AutoService(AOCSolution::class)
class Day10 : AOCSolution {
    override val year = 2015
    override val day = 10

    private fun calculateLength(times: Int, input: String): Int {
        // The solution is basically run length encoding

        // The buffer size is calculated by taking the length of the input, adding 1 to it and multiplying it
        // by a rounded up value of Conways constant to the power of the number of times the sequence is run
        // through the algorithm. This is used for optimal buffer sizing.
        val bufferSize = (input.length + 1) * (1.305).pow(times).toInt()

        var writeBuffer = ByteArray(bufferSize)
        var readBuffer = ByteArray(bufferSize)

        // The length of the input sequence
        var length = 0

        input.forEach {
            writeBuffer[length++] = it.digitToInt().toByte()
        }

        repeat(times) {
            // Swap the buffers, so the former write buffer is now the read buffer, i.e. the previous sequence is read
            val temp = writeBuffer
            writeBuffer = readBuffer
            readBuffer = temp

            var bufferIndex = 0
            var current = readBuffer[0]
            var runLength = 1
            for (i in 1 until length) {
                if (readBuffer[i] == current) {
                    runLength++
                } else {
                    writeBuffer[bufferIndex++] = runLength.toByte()
                    writeBuffer[bufferIndex++] = current
                    current = readBuffer[i]
                    runLength = 1
                }
            }

            writeBuffer[bufferIndex++] = runLength.toByte()
            writeBuffer[bufferIndex++] = current
            length = bufferIndex
        }

        return length
    }

    override fun part1(inputFile: String): String {
        val input = readResource(inputFile).trim()

        val result = calculateLength(40, input)

        return result.toString()
    }

    override fun part2(inputFile: String): String {
        val input = readResource(inputFile).trim()

        val result = calculateLength(50, input)

        return result.toString()
    }
}
