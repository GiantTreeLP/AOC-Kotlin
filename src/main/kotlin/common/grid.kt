package common

import kotlin.math.min

class Grid<T> private constructor(val width: Int, val height: Int, grid: Array<Any?>) : Iterable<Triple<Int, Int, T>> {
    @Suppress("UNCHECKED_CAST")
    private val internalGrid: Array<T> = grid as Array<T>

    val bounds = Rectangle(Point(0, 0), Point(this.width, this.height))

    val values get() = this.internalGrid

    val indices = PointProgression(width.toLong(), height.toLong(), 1)

    constructor(width: Int, height: Int, initializer: (x: Int, y: Int) -> T) : this(
        width,
        height,
        Array<Any?>(width * height) { index ->
            initializer(index % width, index / width)
        }
    )

    constructor(width: Int, height: Int, initializer: (Point) -> T) : this(width, height, { x, y ->
        initializer(Point(x.toLong(), y.toLong()))
    })

    fun getRow(index: Int): Array<T> {
        require(index in 0 until this.height) { "Row index out of bounds" }
        return this.internalGrid.copyOfRange(index * this.width, (index + 1) * this.width)
    }

    fun getColumn(index: Int): List<T> {
        require(index in 0 until this.width) { "Column index out of bounds" }
        @Suppress("UNCHECKED_CAST")
        return (0 until this.height).map { row -> this.internalGrid[row * this.width + index] }
    }

    operator fun get(x: Int, y: Int): T {
        require(x in 0 until this.width) { "X index out of bounds" }
        require(y in 0 until this.height) { "Y index out of bounds" }
        return this.internalGrid[y * this.width + x]
    }

    operator fun get(point: Point): T {
        return this[point.x.toInt(), point.y.toInt()]
    }

    fun getOrNull(x: Int, y: Int): T? {
        if (x !in 0 until this.width || y !in 0 until this.height) {
            return null
        }
        return this.internalGrid[y * this.width + x]
    }

    fun getOrNull(point: Point): T? {
        return this.getOrNull(point.x.toInt(), point.y.toInt())
    }

    fun rows(): List<List<T>> {
        return this.internalGrid.asList().chunked(this.width)
    }

    fun columns(): List<List<T>> {
        return (0 until this.width).map { this.getColumn(it) }
    }

    fun subGrid(x: Int, y: Int, width: Int, height: Int): Grid<T> {
        require(x in 0 until this.width) { "X index out of bounds" }
        require(y in 0 until this.height) { "Y index out of bounds" }
        require(x + width <= this.width) { "Subgrid width out of bounds" }
        require(y + height <= this.height) { "Subgrid height out of bounds" }

        val subGrid = Grid(width, height) { dx, dy ->
            this[y + dy, x + dx]
        }
        return subGrid
    }

    fun subGrids(width: Int, height: Int): List<Grid<T>> {
        return buildList(width * height) {
            for (y in 0..(this@Grid.height - height)) {
                for (x in 0..(this@Grid.width - width)) {
                    add(this@Grid.subGrid(x, y, width, height))
                }
            }
        }
    }

    fun diagonal(offset: Int = 0): List<T> {
        require(offset <= width) { "Offset must be at most the width of the grid" }
        if (offset < 0) {
            require(-offset <= height) { "Offset must be at most the height of the grid" }
        }
        val iterator = if (offset >= 0) {
            // Diagonal / Super-diagonal
            PointIterator(offset.toLong(), 0, width.toLong(), height.toLong(), width + 1L)
        } else {
            // Sub-diagonal
            PointIterator(0, -offset.toLong(), width.toLong(), height.toLong(), width + 1L)
        }
        return buildList {
            for (point in iterator) {
                add(this@Grid[point])
            }
        }
    }

    fun primaryDiagonals(): List<List<T>> {
        val result = mutableListOf<List<T>>()

        // Top left to bottom right
        val diagonal = buildList(min(width, height)) {
            for (p in PointIterator(Point(0, 0), this@Grid, width + 1L)) {
                add(this@Grid[p])
            }
        }

        result.add(diagonal)

        // Top right to bottom left
        val diagonal2 = buildList(min(width, height)) {
            for (p in PointIterator(Point(width - 1, 0), this@Grid, width - 1L)) {
                add(this@Grid[p])
            }
        }
        result.add(diagonal2)

        return result
    }

    operator fun set(x: Int, y: Int, value: T) {
        require(x in 0 until this.width) { "X index out of bounds" }
        require(y in 0 until this.height) { "Y index out of bounds" }
        this.internalGrid[y * this.width + x] = value
    }

    operator fun set(point: Point, value: T) {
        this[point.x.toInt(), point.y.toInt()] = value
    }

    inline fun <U> mapIndexed(crossinline transform: (x: Int, y: Int, T) -> U): Grid<U> {
        return Grid(width, height) { x, y ->
            transform(x, y, this[x, y])
        }
    }

    inline fun <U> mapIndexed(crossinline transform: (point: Point, T) -> U): Grid<U> {
        return this.mapIndexed { x, y, value -> transform(Point(x, y), value) }
    }

    fun flipHorizontal(): Grid<T> {
        return this.mapIndexed { x, y, _ -> this[this.width - x - 1, y] }
    }

    fun flipVertical(): Grid<T> {
        return this.mapIndexed { x, y, _ -> this[x, this.height - y - 1] }
    }

    fun toList(): List<List<T>> {
        return this.internalGrid.asList().chunked(this.width)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Grid<*>) return false

        if (!this.internalGrid.contentEquals(other.internalGrid)) return false

        return true
    }

    override fun hashCode(): Int {
        return this.internalGrid.hashCode()
    }

    override fun toString(): String {
        return buildString {
            this.appendLine("Grid(${this@Grid.width} x ${this@Grid.height}) [")

            val strings = this@Grid.internalGrid.map { it.toString() }
            val maxElementWidth = strings.maxOfOrNull { it.length } ?: 0

            for (rowIndex in 0 until height) {
                this.append("  [")
                for (i in 0 until width) {
                    val element = strings[rowIndex + i * width]
                    this.append(element.padStart(maxElementWidth))
                    if (i < width - 1) {
                        this.append(" ")
                    }
                }
                this.appendLine("]")
            }

            this.appendLine("]")
        }
    }

    override fun iterator(): Iterator<Triple<Int, Int, T>> = GridIterator(this)

    fun pointIterator(): Iterator<Pair<Point, T>> {
        return iterator {
            for ((x, y, value) in this@Grid) {
                yield(Point(x.toLong(), y.toLong()) to value)
            }
        }
    }

    internal class GridIterator<T>(private val grid: Grid<T>) : Iterator<Triple<Int, Int, T>> {
        private val width = this.grid.width
        private val height = this.grid.height
        private var x = 0
        private var y = 0

        override fun next(): Triple<Int, Int, T> {
            if (!this.hasNext()) throw NoSuchElementException()
            val value = Triple(this.x, this.y, this.grid[this.x, this.y])
            this.x++
            if (this.x >= this.width) {
                this.x = 0
                this.y++
            }
            return value
        }

        override fun hasNext(): Boolean {
            require(this.width == this.grid.width && this.height == this.grid.height) { "Grid was modified during iteration" }
            return this.x < this.width && this.y < this.height
        }

    }


    companion object {
        fun <T> Iterable<Iterable<T>>.toGrid(): Grid<T> {
            val outer = this.toList()
            val width = outer.firstOrNull()?.count() ?: 0
            return Grid(width, outer.size) { x, y -> outer[y].elementAt(x) }
        }
    }
}

class PointProgression(
    val width: Long,
    val height: Long,
    val stride: Long = 1
) : Iterable<Point> {
    override fun iterator(): Iterator<Point> {
        return PointIterator(0, 0, width, height, stride)
    }

    infix fun step(stride: Long): PointProgression {
        return PointProgression(width, height, stride)
    }

}

class PointIterator(
    x: Long = 0, y: Long = 0, private val width: Long = 0, height: Long = 0, private val stride: Long = 1
) : Iterator<Point> {

    private var index = y * width + x
    private val lastIndex = width * height

    constructor(
        startPoint: Point, grid: Grid<*>, stride: Long = 1
    ) : this(startPoint.x, startPoint.y, grid.width.toLong(), grid.height.toLong(), stride)

    override fun hasNext(): Boolean {
        return index < lastIndex
    }

    override fun next(): Point {
        return Point(index % width, index / width).also {
            index += stride
        }
    }
}
