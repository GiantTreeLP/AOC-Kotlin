package common.grid

import common.Point
import common.Rectangle
import kotlin.math.min

interface Grid<T> : Iterable<T> {
    val width: Int
    val height: Int
    val bounds: Rectangle
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
}

inline fun <reified T> Iterable<Iterable<T>>.toGrid(): Grid<T> {
    val outer = this.toList()
    val width = outer.maxOf(Iterable<T>::count)
    val gridArray = Array(width * outer.size) { index ->
        val x = index % width
        val y = index / width
        outer[y].elementAt(x)
    }
    return DefaultGrid(width, outer.size, gridArray)
}

inline fun <reified T> Array<out Iterable<T>>.toGrid(): Grid<T> {
    val width = this.maxOf(Iterable<T>::count)
    val gridArray = Array(width * this.size) { index ->
        val x = index % width
        val y = index / width
        this[y].elementAt(x)
    }
    return DefaultGrid(width, this.size, gridArray)
}

inline fun <reified T> Array<Array<T>>.toGrid(): Grid<T> {
    val height = this.size
    val width = this.maxOf(Array<T>::size)

    @Suppress("UNCHECKED_CAST")
    val gridArray = arrayOfNulls<T>(width * height).apply {
        for (y in 0 until height) {
            this@toGrid[y].copyInto(this, y * width)
        }
    } as Array<T>

    return DefaultGrid(width, height, gridArray)
}

inline fun <reified T> DefaultGrid(width: Int, height: Int, initializer: (x: Int, y: Int) -> T): DefaultGrid<T> {
    val grid = arrayOfNulls<T>(width * height)

    for (y in 0 until height) {
        for (x in 0 until width) {
            grid[y * width + x] = initializer(x, y)
        }
    }

    @Suppress("UNCHECKED_CAST")
    return DefaultGrid(
        width,
        height,
        grid as Array<T>
    )
}

inline fun <reified T> DefaultGrid(width: Int, height: Int, initializer: (Point) -> T): DefaultGrid<T> {
    return DefaultGrid(width, height) { x, y ->
        initializer(Point(x, y))
    }
}

fun Grid<Char>.toStrings(): Array<String> {
    return Array(this.height) { y ->
        String(this[y, Char::class.javaObjectType].toCharArray())
    }
}

inline fun <T, reified U> Grid<T>.mapGrid(transform: (T) -> U): Grid<U> {
    return DefaultGrid(width, height) { x, y ->
        transform(this[x, y])
    }
}

inline fun <T, reified U> Grid<T>.mapGridIndexed(transform: (x: Int, y: Int, T) -> U): Grid<U> {
    return DefaultGrid(width, height) { x, y ->
        transform(x, y, this[x, y])
    }
}

inline fun <T, reified U> Grid<T>.mapGridIndexed(transform: (point: Point, T) -> U): Grid<U> {
    return DefaultGrid(width, height) { p -> transform(p, this[p]) }
}


inline fun <reified T> Grid<T>.flipHorizontal(): Grid<T> {
    return this.mapGridIndexed { x, y, _ -> this[this.width - x - 1, y] }
}

inline fun <reified T> Grid<T>.flipVertical(): Grid<T> {
    return this.mapGridIndexed { x, y, _ -> this[x, this.height - y - 1] }
}

inline fun <reified T> Grid<T>.diagonal(offset: Int): Array<T> {
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

inline fun <reified T> Grid<T>.primaryDiagonals(): Pair<Array<T>, Array<T>> {
    val topLeft = Array(min(width, height)) { index ->
        this[index, index]
    }

    val topRight = Array(min(width, height)) { index ->
        this[width - index - 1, index]
    }

    return topLeft to topRight
}

inline fun <reified T> Grid<T>.rows(): Array<Array<T>> {
    return Array(height) { row ->
        this[row, T::class.java]
    }
}

inline fun <reified T> Grid<T>.getColumn(column: Int): Array<T> {
    require(column in 0 until width) { "Column index out of bounds" }
    return Array(height) { row ->
        this[column, row]
    }
}

inline fun <reified T> Grid<T>.subGrid(x: Int, y: Int, width: Int, height: Int): Grid<T> {
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

inline fun <reified T> Grid<T>.subGrids(width: Int, height: Int): Array<Grid<T>> {
    val lastColumn = this.width - width + 1
    val lastRow = this.height - height + 1
    return Array(lastColumn * lastRow) {
        val y = it / lastColumn
        val x = it % lastColumn
        this.subGrid(x, y, width, height)
    }
}

fun <T> Grid<T>.pointIterator(): Iterator<Pair<Point, T>> {
    return iterator {
        for (point in this@pointIterator.indices) {
            yield(point to this@pointIterator[point])
        }
    }
}

inline fun <T> Grid<T>.positionOfFirstOrNull(predicate: (T) -> Boolean): Point? {
    for (y in 0 until height) {
        for (x in 0 until width) {
            if (predicate(this[x, y])) {
                return Point(x, y)
            }
        }
    }

    return null
}

inline fun <T> Grid<T>.positionOfFirst(predicate: (T) -> Boolean): Point {
    return positionOfFirstOrNull(predicate) ?: throw NoSuchElementException()
}
