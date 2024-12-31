package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.EightBitString
import common.readResourceBinary
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
        val input = readResourceBinary(inputFile)

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
            input: ByteArray,
            startNonce: Int,
            private val step: Int,
            private val batchSize: Int,
            private val leadingZeros: Int
        ) : Callable<Int> {
            private val digest = MessageDigest.getInstance("MD5")
            private var nonce = startNonce

            private val digestLength = digest.digestLength
            private var buffer = ByteArray(digestLength)

            private val bytes = ByteArray(input.size + CHARS_FOR_INT)

            private val string = EightBitString(bytes).apply {
                append(input)
                mark()
            }

            override fun call(): Int {
                for (i in 0 until batchSize) {
                    string.reset()
                    string.appendIntAsString(nonce + i)

                    digest.update(bytes, 0, string.position())
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
    }
}
