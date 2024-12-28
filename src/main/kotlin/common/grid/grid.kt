package common.grid

import common.Point
import common.Rectangle
import kotlin.math.min

interface Grid<T : Any> : Iterable<T> {
    val width: Int
    val height: Int
    val bounds: Rectangle
    val values: Array<T>
    val indices: PointProgression

    operator fun get(row: Int, arrayType: Class<T>): Array<T>
    operator fun get(x: Int, y: Int): T
    operator fun get(point: Point): T
    fun getOrNull(x: Int, y: Int): T?
    fun getOrNull(point: Point): T?

    operator fun set(x: Int, y: Int, value: T)
    operator fun set(point: Point, value: T)
    operator fun set(row: Int, value: Array<T>)

    fun transposed(): Grid<T>

    override fun iterator(): Iterator<T> = GridIterator(this)
    fun pointIterator(): Iterator<Pair<Point, T>>
    fun firstSample() = this.values.first()
}

inline fun <reified T : Any> Iterable<Iterable<T>>.toGrid(): Grid<T> {
    val outer = this.toList()
    val width = outer.first().count()
    val gridArray = Array(width * outer.size) { index ->
        val x = index % width
        val y = index / width
        outer[y].elementAt(x)
    }
    return DefaultGrid(width, outer.size, gridArray)
}

fun Grid<Char>.toStrings(): Array<String> {
    return Array(this.height) { y ->
        String(this[y, Char::class.javaObjectType].toCharArray())
    }
}

inline fun <T : Any, reified U : Any> Grid<T>.map(transform: (T) -> U): Grid<U> {
    val gridArray = Array(width * height) { index ->
        transform(this.values[index])
    }

    return DefaultGrid(width, height, gridArray)
}

inline fun <T : Any, reified U : Any> Grid<T>.mapIndexed(transform: (x: Int, y: Int, T) -> U): Grid<U> {
    val gridArray = Array(width * height) { index ->
        val x = index % width
        val y = index / width
        transform(x, y, this[x, y])
    }

    return DefaultGrid(width, height, gridArray)
}

inline fun <T : Any, reified U : Any> Grid<T>.mapIndexed(transform: (point: Point, T) -> U): Grid<U> {
    return this.mapIndexed { x, y, value -> transform(Point(x, y), value) }
}


inline fun <reified T : Any> Grid<T>.flipHorizontal(): Grid<T> {
    return this.mapIndexed { x, y, _ -> this[this.width - x - 1, y] }
}

inline fun <reified T : Any> Grid<T>.flipVertical(): Grid<T> {
    return this.mapIndexed { x, y, _ -> this[x, this.height - y - 1] }
}

inline fun <reified T : Any> Grid<T>.diagonal(offset: Int): Array<T> {
    require(offset < width) { "Offset must be in the range of the width of the grid" }
    if (offset < 0) {
        require(-offset < height) { "Offset must be in the range of the height of the grid" }
    }
    val iterator = if (offset >= 0) {
        // Diagonal / Super-diagonal
        PointIterator(offset.toLong(), 0, width.toLong(), height.toLong(), width + 1L)
    } else {
        // Sub-diagonal
        PointIterator(0, -offset.toLong(), width.toLong(), height.toLong(), width + 1L)
    }
    return Array(min(width - offset, height + offset)) {
        val point = iterator.next()
        this[point.x.toInt(), point.y.toInt()]
    }
}

inline fun <reified T : Any> Grid<T>.primaryDiagonals(): Pair<Array<T>, Array<T>> {
    val topLeft = Array(min(width, height)) { index ->
        this[index, index]
    }

    val topRight = Array(min(width, height)) { index ->
        this[width - index - 1, index]
    }

    return topLeft to topRight
}

inline fun <reified T : Any> Grid<T>.rows(): Array<Array<T>> {
    return Array(height) { row ->
        this[row, T::class.java]
    }
}

inline fun <reified T : Any> Grid<T>.getColumn(column: Int): Array<T> {
    require(column in 0 until width) { "Column index out of bounds" }
    return Array(height) { row ->
        this[column, row]
    }
}

inline fun <reified T : Any> Grid<T>.subGrid(x: Int, y: Int, width: Int, height: Int): Grid<T> {
    require(x in 0 until this.width) { "X index out of bounds" }
    require(y in 0 until this.height) { "Y index out of bounds" }
    require(x + width <= this.width) { "Subgrid width out of bounds" }
    require(y + height <= this.height) { "Subgrid height out of bounds" }

    val subGridArray = Array(width * height) { index ->
        val dx = index % width
        val dy = index / width
        this[y + dy, x + dx]
    }

    return DefaultGrid(width, height, subGridArray)
}

inline fun <reified T : Any> Grid<T>.subGrids(width: Int, height: Int): Array<Grid<T>> {
    val lastColumn = this.width - width + 1
    val lastRow = this.height - height + 1
    return Array(lastColumn * lastRow) {
        val y = it / lastColumn
        val x = it % lastColumn
        this.subGrid(x, y, width, height)
    }
}