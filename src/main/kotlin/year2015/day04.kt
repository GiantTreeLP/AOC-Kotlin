package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResource
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.concurrent.Callable
import java.util.concurrent.Executors

@AutoService(AOCSolution::class)
class Day04 : AOCSolution {
    override val year = 2015
    override val day = 4

    override fun part1(inputFile: String): String {
        return solve(inputFile, 5)
    }

    override fun part2(inputFile: String): String {
        return solve(inputFile, 6)

    }

    private fun solve(inputFile: String, leadingZeroes: Int): String {
        val input = readResource(inputFile)

        val hashers = List(CONCURRENCY) {
            Hasher(input, it * BATCH_SIZE, CONCURRENCY, BATCH_SIZE, leadingZeroes)
        }
        val executor = Executors.newFixedThreadPool(CONCURRENCY)

        do {
            val results = executor.invokeAll(hashers)
            val result = results.firstOrNull { it.get() != -1 }?.get()
            if (result != null) {
                executor.shutdownNow()
                return result.toString()
            }
        } while (true)
    }

    companion object {
        private const val CONCURRENCY = 8
        private const val BATCH_SIZE = 100_000
        private const val CHARS_FOR_INT = 10

        private class Hasher(
            input: String,
            startNonce: Int,
            private val step: Int,
            private val batchSize: Int,
            private val leadingZeros: Int
        ) : Callable<Int> {
            private val digest = MessageDigest.getInstance("MD5")
            private var nonce = startNonce

            private val digestLength = digest.digestLength
            private var buffer = ByteArray(digestLength)

            private val bytes = ByteArray(input.length + CHARS_FOR_INT)

            private val byteBuffer = ByteBuffer.wrap(bytes)
                .put(input.toByteArray(Charsets.US_ASCII))
                .mark()

            override fun call(): Int {
                for (i in 0 until batchSize) {
                    byteBuffer.reset()
                    byteBuffer.putIntAsString(nonce + i)

                    digest.update(bytes, 0, byteBuffer.position())
                    digest.digest(buffer, 0, digestLength)
                    val startsWithHexZeroes = startsWithHexZeroes()
                    if (startsWithHexZeroes) {
                        return nonce + i
                    }
                }
                nonce += step * batchSize
                return -1
            }

            private fun startsWithHexZeroes(): Boolean {
                val halfByte = leadingZeros and 1 == 1
                val fullBytes = (leadingZeros - (leadingZeros and 1)) / 2
                for (i in 0 until fullBytes) {
                    if (buffer[i] != 0.toByte()) return false
                }
                if (halfByte) {
                    return buffer[fullBytes].toInt() and 0xF0 == 0
                }
                return true
            }
        }

        private val digits = byteArrayOf(
            '0'.code.toByte(),
            '1'.code.toByte(),
            '2'.code.toByte(),
            '3'.code.toByte(),
            '4'.code.toByte(),
            '5'.code.toByte(),
            '6'.code.toByte(),
            '7'.code.toByte(),
            '8'.code.toByte(),
            '9'.code.toByte()
        )

        private fun intStringLength(value: Int): Int {
            return when (value) {
                in 0 until 10 -> 1
                in 10 until 100 -> 2
                in 100 until 1000 -> 3
                in 1000 until 10000 -> 4
                in 10000 until 100000 -> 5
                in 100000 until 1000000 -> 6
                in 1000000 until 10000000 -> 7
                in 10000000 until 100000000 -> 8
                in 100000000 until 1000000000 -> 9
                else -> 10
            }
        }

        private fun ByteBuffer.putIntAsString(value: Int) {
            var remaining = value
            val offset = position() - 1
            val length = intStringLength(remaining)
            for (i in length downTo 1) {
                val digit = remaining % 10
                remaining /= 10
                put(offset + i, digits[digit])
            }
            this.position(offset + length + 1)
        }
    }
}
