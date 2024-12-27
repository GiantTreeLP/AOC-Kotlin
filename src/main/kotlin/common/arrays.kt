package common

fun IntArray.dropFromArray(n: Int): IntArray {
    return this.copyOfRange(n, this.size)
}

inline fun LongArray.mapToIntArray(transform: (Long) -> Int): IntArray {
    return IntArray(this.size) { transform(this[it]) }
}

inline fun IntArray.zipWithNext(transform: (current: Int, next: Int) -> Int): IntArray {
    return IntArray(this.size - 1) { transform(this[it], this[it + 1]) }
}

inline fun IntArray.mapWindowed(size: Int, transform: (array: IntArray, from: Int, to: Int) -> Int): IntArray {
    return IntArray(this.size - size + 1) {
        transform(this, it, it + size)
    }
}

inline fun <reified T, reified U> Array<T>.mapArray(transform: (T) -> U): Array<U> {
    return Array(this.size) { transform(this[it]) }
}