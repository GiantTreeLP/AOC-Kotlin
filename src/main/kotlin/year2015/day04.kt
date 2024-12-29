package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResource
import java.security.MessageDigest
import kotlin.experimental.and
import kotlin.math.ceil
import kotlin.math.log10

@AutoService(AOCSolution::class)
class Day04 : AOCSolution {
    override val year = 2015
    override val day = 4

    override fun part1(inputFile: String): String {
        val input = readResource(inputFile)

        var nonce = 0
        val digest = MessageDigest.getInstance("MD5")

        do {
            val hash = digest.digest("$input$nonce".encodeToByteArray())
            nonce++
        } while (!hash.startsWithHexZeroes(5))

        return (nonce - 1).toString()
    }

    override fun part2(inputFile: String): String {
        val input = readResource(inputFile)
        val buffer = ByteArray(input.length + ceil(log10(Int.MAX_VALUE.toDouble())).toInt())
        input.encodeToByteArray().copyInto(buffer)

        var nonce = -1
        val digest = MessageDigest.getInstance("MD5")

        do {
            nonce++
            val end = transferIntToByteArrayBase10(nonce, buffer, input.length)
            val digestInput = buffer.copyOfRange(0, end)
            val hash = digest.digest(digestInput)
        } while (!hash.startsWithHexZeroes(6))

        return nonce.toString()
    }

    private fun transferIntToByteArrayBase10(int: Int, array: ByteArray, offset: Int): Int {
        val end = offset + stringSize(int)
        var remaining = int
        val asciiOffset = '0'.code
        for (i in end downTo offset + 1) {
            val digit = remaining % 10
            remaining /= 10
            array[i - 1] = (digit + asciiOffset).toByte()
        }
        return end
    }

    private fun stringSize(x: Int): Int {
        var number = x
        var d = 1
        if (number >= 0) {
            d = 0
            number = -number
        }
        var p = -10
        for (i in 1..9) {
            if (number > p) return i + d
            p *= 10
        }
        return 10 + d
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
