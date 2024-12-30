package common

import java.io.BufferedReader
import java.io.InputStream
import java.net.URL
import kotlin.math.sqrt

val spaceSplitRegex = Regex("""\s+""")
private val twoPartRegex = Regex("""\r?\n\r?\n""")

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
    return getResourceAsStream(path).bufferedReader().readLines().filter { it.isNotEmpty() }
}

fun readResourceTwoParts(path: String): Pair<String, String> {
    val input = readResource(path).split(twoPartRegex)
    require(input.size == 2) { "Unexpected input size" }
    return Pair(input[0], input[1])
}

fun readResourceBinary(path: String): ByteArray {
    return getResource(path).openStream().use { it.readBytes() }
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
        first.indices.map { col -> this.map { it[col] } }
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

fun DoubleArray.variance(): Double {
    val mean = average()
    var sum = 0.0
    var count = 0
    for (element in this) {
        val diff = element - mean
        sum += diff * diff
        count++
    }
    return if (count > 1) {
        sum / (count - 1)
    } else {
        Double.NaN
    }
}

fun DoubleArray.standardDeviation(): Double {
    return sqrt(variance())
}
