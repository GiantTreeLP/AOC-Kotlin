package common

import java.io.BufferedReader
import java.io.InputStream
import java.net.URL
import kotlin.math.sqrt

val splitRegex = Regex("""\s+""")

fun getResource(path: String): URL {
    val resource = Thread.currentThread().contextClassLoader.getResource(path)
    requireNotNull(resource) { "Resource $path not found" }
    return resource
}

fun getResourceAsStream(path: String): InputStream {
    val resource = Thread.currentThread().contextClassLoader.getResourceAsStream(path)
    requireNotNull(resource) { "Resource $path not found" }
    return resource
}

fun readResource(path: String): String {
    return getResourceAsStream(path).bufferedReader().use(BufferedReader::readText)
}

fun readResourceLines(path: String): List<String> {
    return getResourceAsStream(path).bufferedReader().readLines()
}

fun isResourceAvailable(path: String): Boolean {
    return getResource(path).openStream().use { true }
}

fun <T> List<List<T>>.transpose(): List<List<T>> {
    // Check that all rows have the same length
    val first = first()
    require(isEmpty() || all { it.size == first.size }) { "All rows must have the same length" }
    return if (isEmpty() || first.isEmpty()) {
        emptyList()
    } else {
        // Transpose the list
        // For each column, map the list of rows to the column
        (0 until first.size).map { col -> this.map { it[col] } }
    }
}

fun <T> List<List<T>>.pairs(): List<Pair<T, T>> {
    require(this.all { it.size == 2 }) { "All rows must have exactly two elements" }
    return this.map { Pair(it[0], it[1]) }
}

fun <T> Sequence<List<T>>.pairs(): Sequence<Pair<T, T>> {
    return this.map { Pair(it[0], it[1]) }
}

fun <T> List<Pair<T, T>>.unzipWithNext(): List<T> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyList()

    val result = mutableListOf<T>()
    var current: Pair<T, T>? = null
    while (iterator.hasNext()) {
        current = iterator.next()
        result.add(current.first)
    }
    if (current != null) {
        result.add(current.second)
    }
    return result
}


fun <T> List<List<T>>.chunks(size: Int) = this.chunks(size, size)

/**
 * Split the 2-D list into chunks of the given dimensions.
 *
 * @param width The width of the chunks
 * @param height The height of the chunks
 * @return A list of chunks
 */
fun <T> List<List<T>>.chunks(width: Int, height: Int): List<List<List<T>>> {
    require(width > 0) { "Width must be positive" }
    require(height > 0) { "Height must be positive" }

    val result = mutableListOf<List<List<T>>>()

    for (y in 0..(this.size - height)) {
        for (x in 0..(this[y].size - width)) {
            // x, y is the top-left corner of the chunk
            val chunk = mutableListOf<List<T>>()
            for (dy in 0 until height) {
                chunk.add(this[y + dy].subList(x, x + width))
            }
            result.add(chunk)
        }
    }

    return result
}

fun <T> List<List<T>>.primaryDiagonals(): List<List<T>> {
    require(all { it.size == size }) { "Grid must be a square" }

    val result = mutableListOf<List<T>>()

    // Top left to bottom right
    val diagonal = mutableListOf<T>()
    // Top right to bottom left
    val diagonal2 = mutableListOf<T>()
    for (i in this.indices) {
        diagonal.add(this[i][i])
        diagonal2.add(this[i][size - i - 1])
    }
    result.add(diagonal)
    result.add(diagonal2)

    return result
}

fun <T> Iterable<T>.printEach() = onEach { println(it) }

fun <T> List<T>.isSorted(comparator: Comparator<T>): Boolean {
    for (i in 0 until size - 1) {
        if (comparator.compare(this[i], this[i + 1]) > 0) {
            return false
        }
    }
    return true
}

fun <K, V> Sequence<Pair<K, V>>.toMultiMap(): Map<K, List<V>> {
    return this.groupBy({ it.first }, { it.second })
}

fun <T : Number> Iterable<T>.mean(): Double {
    var sum = 0.0
    var count = 0
    for (element in this) {
        sum += element.toDouble()
        count++
        if (count < 0) {
            throw ArithmeticException("Count overflow")
        }
    }
    return if (count > 0) {
        sum / count
    } else {
        Double.NaN
    }
}

fun <T : Number> Iterable<T>.variance(): Double {
    val mean = mean()
    var sum = 0.0
    var count = 0
    for (element in this) {
        val diff = element.toDouble() - mean
        sum += diff * diff
        count++
        if (count < 0) {
            throw ArithmeticException("Count overflow")
        }
    }
    return if (count > 1) {
        sum / (count - 1)
    } else {
        Double.NaN
    }
}

fun <T : Number> Iterable<T>.standardDeviation(): Double {
    return sqrt(variance())
}