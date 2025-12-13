package common

import java.nio.ByteBuffer
import java.nio.CharBuffer


@Suppress("RedundantVisibilityModifier")
class EightBitString private constructor(
    private var buffer: ByteBuffer,
) : Appendable, CharSequence {
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
        return if (buffer.hasArray() &&
            buffer.arrayOffset() == 0 &&
            buffer.limit() == buffer.capacity() &&
            buffer.array().size == buffer.capacity()
        ) {
            buffer.array()
        } else {
            resetScope { ebs ->
                val array = ByteArray(buffer.remaining())
                buffer.get(array)
                array
            }
        }
    }

    public fun array(destination: ByteArray, offset: Int = 0) {
        val length = minOf(limit(), destination.size - offset)
        if (buffer.hasArray()) {
            System.arraycopy(buffer.array(), buffer.arrayOffset() + buffer.position(), destination, offset, length)
        } else {
            flipScope { ebs ->
                ebs.buffer.get(destination, offset, length)
            }
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

    override fun append(csq: CharSequence?): EightBitString {
        when (csq) {
            null -> append("null")
            is EightBitString -> append(csq)
            is String -> append(csq)
            else -> {
                growIfNeeded(csq.length)
                val buf = CharBuffer.wrap(csq)
                append(Charsets.US_ASCII.encode(buf))
            }
        }
        return this
    }

    override fun append(csq: CharSequence?, start: Int, end: Int): EightBitString {
        when (csq) {
            null -> append("null", start, end)
            is String -> append(csq.substring(start, end))
            else -> {
                growIfNeeded(end - start)
                val buf = CharBuffer.wrap(csq, start, end)
                append(Charsets.US_ASCII.encode(buf))
            }
        }
        return this
    }

    public override fun append(value: Char): EightBitString {
        growIfNeeded(2)
        buffer.putChar(value)
        return this
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
        val length = stringSize(value)
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
        val offset = position()
        val length = stringSize(value)
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

    public fun getByte(index: Int): Byte {
        return buffer.get(index)
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
        if (mark >= 0) {
            newBuffer.position(mark)
            newBuffer.mark()
        }
        newBuffer.position(position)
        buffer = newBuffer
    }

    private fun grow() {
        growExact((buffer.capacity() * GROWTH_FACTOR).toInt())
    }

    override val length: Int
        get() = buffer.limit()

    override fun get(index: Int): Char {
        return buffer.get(index).toInt().toChar()
    }

    override fun subSequence(startIndex: Int, endIndex: Int): EightBitString {
        return EightBitString(buffer.slice(startIndex, endIndex - startIndex))
    }

    /**
     * Executes [action] inside a "scope" that has the current [buffer] flipped.
     * The [buffer]'s flipping is undone after the action.
     *
     * @param O the result type of the action
     * @param action the action to execute
     * @return the result of [action]
     */
    private inline fun <O : Any?> EightBitString.flipScope(action: (EightBitString) -> O): O {
        // Save positions, marks and limits
        val position = this.position()
        val limit = this.limit()
        val mark = this.mark

        this.flip()
        val result = action(this)

        this.limit(limit)

        if (mark >= 0) {
            this.position(mark)
            this.mark()
        }
        this.position(position)

        return result
    }

    /**
     * Executes [action] inside a "scope" that has the current [buffer]'s position set to 0.
     * Modifications done to the [buffer] are undone after the action.
     *
     * @param O the result type of the action
     * @param action the action to execute
     * @return the result of [action]
     */
    private inline fun <O : Any?> EightBitString.resetScope(action: (EightBitString) -> O): O {
        // Save positions, marks and limits
        val position = this.position()
        val limit = this.limit()
        val mark = this.mark

        this.position(0)
        val result = action(this)

        this.limit(limit)

        if (mark >= 0) {
            this.position(mark)
            this.mark()
        }
        this.position(position)

        return result
    }


    fun contentEquals(other: EightBitString): Boolean {
        if (this === other) return true

        return this.flipScope { first ->
            other.flipScope { second ->
                first.buffer == second.buffer
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EightBitString) return false

        if (mark != other.mark) return false
        if (buffer != other.buffer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mark
        result = 31 * result + buffer.hashCode()
        return result
    }

    override fun toString(): String {
        return String(array(), Charsets.US_ASCII)
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

        /**
         * Returns the string representation size for a given int value.
         *
         * @param x int value
         * @return string size
         *
         * @implNote There are other ways to compute this: e.g. binary search,
         * but values are biased heavily towards zero, and therefore linear search
         * wins. The iteration results are also routinely inlined in the generated
         * code after loop unrolling.
         */
        public fun stringSize(x: Int): Int {
            var x = x
            var d = 1
            if (x >= 0) {
                d = 0
                x = -x
            }
            var p = -10
            for (i in 1..9) {
                if (x > p) return i + d
                p *= 10
            }
            return 10 + d
        }

        /**
         * Returns the string representation size for a given long value.
         *
         * @param x long value
         * @return string size
         *
         * @implNote There are other ways to compute this: e.g. binary search,
         * but values are biased heavily towards zero, and therefore linear search
         * wins. The iteration results are also routinely inlined in the generated
         * code after loop unrolling.
         */
        public fun stringSize(x: Long): Int {
            var x = x
            var d = 1
            if (x >= 0) {
                d = 0
                x = -x
            }
            var p: Long = -10
            for (i in 1..18) {
                if (x > p) return i + d
                p *= 10
            }
            return 19 + d
        }
    }
}
