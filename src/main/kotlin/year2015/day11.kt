package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceLines
import java.nio.ByteBuffer

@AutoService(AOCSolution::class)
class Day11 : AOCSolution {
    override val year = 2015
    override val day = 11

    /**
     * This method checks whether a password contains any of the forbidden letters and if so, it increments the password
     * at that position and fills the rest with 'a's.
     * If the password does not contain any forbidden letters, it simply increments the password (see [getNextPassword]).
     */
    private fun getNextPasswordSlow(password: Long): Long {
        var shift = Long.SIZE_BITS - 8

        if (longHasByte(password, 'i'.code.toByte()) ||
            longHasByte(password, 'o'.code.toByte()) ||
            longHasByte(password, 'l'.code.toByte())
        ) {
            while (shift >= 0) {
                val mask = 0xFFL shl shift
                val letter = ((password and mask) ushr shift).toInt()

                when (letter) {
                    'i'.code, 'o'.code, 'l'.code -> {
                        val aFillMask = (1L shl shift) - 1L
                        var result = password + (1L shl shift)
                        result = result and aFillMask.inv() or (A_FILL and aFillMask) // Fill with 'a'
                        return result
                    }
                }

                shift -= 8
            }
        } else {
            return getNextPassword(password)
        }

        return password
    }

    private fun getNextPassword(password: Long): Long {
        var shift = 0
        var result = password
        while (shift < 64) {
            val mask = 0xFFL shl shift
            val letter = ((result and mask) ushr shift).toInt()
            when (letter) {
                'z'.code -> {
                    result = (result and mask.inv()) or (A_FILL and mask)
                }

                else -> {
                    result += 1L shl shift
                    break
                }

            }
            shift += 8
        }
        return result
    }

    private fun isValidPassword(password: Long): Boolean {
        // Passwords are not allowed to contain the letters i, o, or l
        var result = password
        if (longHasByte(result, 'i'.code.toByte()) ||
            longHasByte(result, 'o'.code.toByte()) ||
            longHasByte(result, 'l'.code.toByte())
        ) {
            return false
        }

        // Passwords must include one increasing straight of at least three letters
        result = password
        var hasStraight = false
        var lastLetter = result and 0xFF
        result = result ushr 8
        var straightCount = 1
        while (result != 0L) {
            val letter = result and 0xFF
            if (letter == lastLetter - 1) {
                straightCount++
                if (straightCount == 3) {
                    hasStraight = true
                    break
                }
            } else {
                straightCount = 1
            }
            lastLetter = letter
            result = result ushr 8
        }
        if (!hasStraight) {
            return false
        }

        // Passwords must contain at least two different, non-overlapping pairs of letters
        result = password
        var pairCount = 0
        var lastPair = 0L
        while (result != 0L) {
            val letter = result and 0xFF
            if (letter == lastPair) {
                pairCount++
                lastPair = 0L
                if (pairCount == 2) {
                    break
                }
            } else {
                lastPair = letter
            }
            result = result ushr 8
        }

        return pairCount == 2
    }

    override fun part1(inputFile: String): String {
        val passwordCandidates = readResourceLines(inputFile)

        val result = passwordCandidates.map { passwordCandidate ->
            var password = passwordToLong(passwordCandidate.encodeToByteArray())

            password = getNextPasswordSlow(password)

            while (!isValidPassword(password)) {
                password = getNextPassword(password)
            }

            String(longToPassword(password))
        }

        return result.joinToString(", ")
    }

    override fun part2(inputFile: String): String {

        val passwordCandidates = readResourceLines(inputFile)

        val result = passwordCandidates.map { passwordCandidate ->
            var password = passwordToLong(passwordCandidate.encodeToByteArray())
            // Get the next password that is valid
            password = getNextPasswordSlow(password)
            while (!isValidPassword(password)) {
                password = getNextPassword(password)
            }

            password = getNextPassword(password)

            // Get the password that is valid after the current one
            while (!isValidPassword(password)) {
                password = getNextPassword(password)
            }

            String(longToPassword(password))
        }

        return result.joinToString(", ")
    }

    companion object {
        const val A_FILL = 0x6161616161616161L // 'a' repeated 8 times

        private fun passwordToLong(password: ByteArray): Long {
            require(password.size == 8) { "Password must be exactly 8 bytes long" }
            var result = 0L
            for (letter in password) {
                result = (result shl 8) or letter.toLong()
            }
            return result
        }

        private fun longToPassword(password: Long): ByteArray {
            val result = ByteArray(8)
            ByteBuffer.wrap(result).putLong(password)
            return result
        }

        // Taken from and adapted to Kotlin from https://graphics.stanford.edu/~seander/bithacks.html#ValueInWord
        // Additionally adapted to work with Longs and introduce a constant to avoid unsigned operations
        private fun longHasByte(password: Long, byte: Byte): Boolean {
            // (haszero((x) ^ (~0UL/255 * (n))))
            return longHasZero(password xor (0x0101010101010101L * byte))
        }

        // Taken from and adapted to Kotlin from https://graphics.stanford.edu/~seander/bithacks.html#ZeroInWord
        // Additionally adapted to work with Longs
        private fun longHasZero(password: Long): Boolean {
            // (((v) - 0x01010101UL) & ~(v) & 0x80808080UL)
            return (password - 0x0101010101010101L) and password.inv() and 0x8080808080808080UL.toLong() != 0L
        }

    }
}
