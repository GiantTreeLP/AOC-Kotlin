package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResource
import java.security.MessageDigest
import kotlin.experimental.and

@AutoService(AOCSolution::class)
class Day04 : AOCSolution {
    override val year = 2015
    override val day = 4

    override fun part1(inputFile: String): String {
        val input = readResource(inputFile)

        var nonce = -1
        val digest = MessageDigest.getInstance("MD5")

        do {
            nonce++
            val hash = digest.digest("$input$nonce".encodeToByteArray())
        } while (!hash.startsWithHexZeroes(5))

        return nonce.toString()
    }

    override fun part2(inputFile: String): String {
        val input = readResource(inputFile)

        var nonce = -1
        val digest = MessageDigest.getInstance("MD5")

        do {
            nonce++
            val hash = digest.digest("$input$nonce".encodeToByteArray())
        } while (!hash.startsWithHexZeroes(6))

        return nonce.toString()
    }
}

private fun ByteArray.startsWithHexZeroes(leadingZeros: Int): Boolean {
    val halfByte = leadingZeros and 1 == 1
    val fullBytes = (leadingZeros - (leadingZeros and 1)) / 2
    for (i in 0 until fullBytes) {
        if (this[i] != 0.toByte()) return false
    }
    if (halfByte) {
        return this[fullBytes] and 0xF0.toByte() == 0.toByte()
    }
    return true
}
