package common

import java.nio.ByteBuffer
import java.nio.charset.Charset
import kotlin.math.log10


@Suppress("RedundantVisibilityModifier")
class EightBitString private constructor(
    private var buffer: ByteBuffer
) {
    constructor(initialCapacity: Int = INITIAL_BUFFER_SIZE) : this(ByteBuffer.allocateDirect(initialCapacity))

    constructor(wrapping: ByteArray) : this(ByteBuffer.wrap(wrapping))

    private var mark = -1

    public fun mark() {
        buffer.mark()
        mark = buffer.position()
    }

    public fun reset() {
        buffer.reset()
    }

    public fun flip() {
        buffer.flip()
        mark = -1
    }

    public fun clear() {
        buffer.clear()
        mark = -1
    }

    public fun compact() {
        buffer.compact()
        mark = -1
    }

    public fun remaining(): Int {
        return buffer.remaining()
    }

    public fun position(): Int {
        return buffer.position()
    }

    public fun position(newPosition: Int) {
        buffer.position(newPosition)
        if (mark > newPosition) {
            mark = -1
        }
    }

    public fun limit(): Int {
        return buffer.limit()
    }

    public fun limit(newLimit: Int) {
        buffer.limit(newLimit)
    }

    public fun capacity(): Int {
        return buffer.capacity()
    }

    public fun array(): ByteArray {
        if (buffer.hasArray()) {
            return buffer.array()
        } else {
            val position = buffer.position()
            buffer.flip()
            val array = ByteArray(buffer.remaining())
            buffer.get(array)

            buffer.position(mark)
            buffer.mark()
            buffer.position(position)
            return array
        }
    }

    public fun array(destination: ByteArray, offset: Int = 0) {
        if (buffer.hasArray()) {
            System.arraycopy(buffer.array(), buffer.arrayOffset() + buffer.position(), destination, offset, limit())
        } else {
            val position = buffer.position()
            buffer.flip()
            buffer.get(destination, offset, limit())

            buffer.position(mark)
            buffer.mark()
            buffer.position(position)
        }
    }

    // Append methods

    public fun append(value: Byte) {
        growIfNeeded(1)
        buffer.put(value)
    }

    public fun append(value: ByteArray) {
        growIfNeeded(value.size)
        buffer.put(value)
    }

    public fun append(value: ByteBuffer) {
        if (buffer.remaining() < value.remaining()) {
            growExact(buffer.capacity() + value.remaining())
        }
        buffer.put(value)
    }

    public fun append(value: Char) {
        growIfNeeded(2)
        buffer.putChar(value)
    }

    public fun append(value: String) {
        append(value.encodeToByteArray())
    }

    public fun append(value: EightBitString) {
        append(value.buffer)
    }

    public fun append(value: Int) {
        growIfNeeded(4)
        buffer.putInt(value)
    }

    public fun appendIntAsString(value: Int) {
        require(value >= 0) { "Negative values are not supported" }
        var remaining = value
        val offset = position()
        val length = if (value != 0) {
            log10(remaining.toDouble()).toInt() + 1
        } else {
            1
        }
        growIfNeeded(length)

        var remainingDigits = length
        while (remainingDigits >= 4) {
            val digitQuad = remaining % 10000
            remaining /= 10000
            buffer.putInt(offset + remainingDigits - 4, digitQuads[digitQuad])
            remainingDigits -= 4
        }

        if (remainingDigits >= 2) {
            val digitPair = remaining % 100
            remaining /= 100
            buffer.putShort(offset + remainingDigits - 2, digitPairs[digitPair])
            remainingDigits -= 2
        }

        if (remainingDigits == 1) {
            buffer.put(offset, ('0'.code + remaining).toByte())
        }
        this.position(offset + length)
    }

    public fun appendLongAsString(value: Long) {
        require(value >= 0) { "Negative values are not supported" }
        var remaining = value
        val offset = position() - 1
        val length = if (value != 0L) {
            log10(remaining.toDouble()).toInt() + 1
        } else {
            1
        }
        growIfNeeded(length)

        var remainingDigits = length
        while (remainingDigits >= 4) {
            val digitQuad = (remaining % 10000).toInt()
            remaining /= 10000
            buffer.putInt(offset + remainingDigits - 4, digitQuads[digitQuad])
            remainingDigits -= 4
        }

        if (remainingDigits >= 2) {
            val digitPair = (remaining % 100).toInt()
            remaining /= 100
            buffer.putShort(offset + remainingDigits - 2, digitPairs[digitPair])
            remainingDigits -= 2
        }

        if (remainingDigits == 1) {
            buffer.put(offset, ('0'.code + remaining).toByte())
        }
        this.position(offset + length)
    }

    public fun appendCharAsByte(value: Char) {
        append(value.code.toByte())
    }

    // Read methods

    public fun get(): Byte {
        return buffer.get()
    }

    public fun getChar(): Char {
        return buffer.char
    }

    public fun getInt(): Int {
        return buffer.int
    }

    public fun getLong(): Long {
        return buffer.long
    }

    public fun getBytes(): ByteArray {
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return bytes
    }

    public fun getString(charset: Charset = Charsets.US_ASCII): String {
        return String(getBytes(), charset)
    }

    // Resize methods

    private fun growIfNeeded(minimumSize: Int) {
        if (buffer.remaining() < minimumSize) {
            grow()
        }
    }

    private fun growExact(newCapacity: Int) {
        val newBuffer = ByteBuffer.allocateDirect(newCapacity)
        val position = buffer.position()
        buffer.flip()
        newBuffer.put(buffer)
        newBuffer.position(mark)
        newBuffer.mark()
        newBuffer.position(position)
        buffer = newBuffer
    }

    private fun grow() {
        growExact((buffer.capacity() * GROWTH_FACTOR).toInt())
    }

    companion object {
        private const val INITIAL_BUFFER_SIZE = 16
        private const val GROWTH_FACTOR = 1.5

        private val digitPairs = ShortArray(100)
        private val digitQuads = IntArray(10_000)

        init {
            for (i in 0..99) {
                val tens = i / 10
                val ones = i % 10
                this.digitPairs[i] = ((('0'.code + tens) shl 8) or ('0'.code + ones)).toShort()
            }

            for (i in 0..9999) {
                val thousands = i / 1000
                val hundreds = (i / 100) % 10
                val tens = (i / 10) % 10
                val ones = i % 10
                this.digitQuads[i] =
                    ((('0'.code + thousands) shl 24) or (('0'.code + hundreds) shl 16) or (('0'.code + tens) shl 8) or ('0'.code + ones))
            }
        }
    }
}