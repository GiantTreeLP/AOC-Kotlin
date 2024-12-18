package common

class Grid<T> private constructor(grid: MutableList<MutableList<T>>) : Iterable<Triple<Int, Int, T>> {
    private val internalGrid: MutableList<MutableList<T>> = grid
    val width get() = this.internalGrid.firstOrNull()?.size ?: 0
    val height get() = this.internalGrid.size
    val bounds get() = Rectangle(Point(0, 0), Point(this.width.toLong(), this.height.toLong()))

    constructor() : this(mutableListOf())

    constructor(width: Int, height: Int, initializer: (x: Int, y: Int) -> T) : this() {
        for (y in 0 until height) {
            val row = MutableList(width) { x -> initializer(x, y) }
            this.addRow(row)
        }
    }

    constructor(width: Int, height: Int, initializer: (Point) -> T) : this(width, height, { x, y ->
        initializer(Point(x.toLong(), y.toLong()))
    })

    fun addRow(row: Iterable<T>) {
        val row = row.toMutableList()
        require(row.size == this.width || this.width == 0) { "Row size must match the grid width" }
        this.internalGrid.add(row)
    }

    fun addColumn(column: List<T>) {
        require(column.size == this.height || this.height == 0) { "Column size must match the grid height" }
        column.forEachIndexed { index, value ->
            if (index >= this.internalGrid.size) {
                this.internalGrid.add(mutableListOf())
            }
            this.internalGrid[index].add(value)
        }
    }

    fun getRow(index: Int): List<T> {
        require(index in 0 until this.height) { "Row index out of bounds" }
        return this.internalGrid[index]
    }

    fun getColumn(index: Int): List<T> {
        require(index in 0 until this.width) { "Column index out of bounds" }
        return this.internalGrid.map { it[index] }
    }

    operator fun get(x: Int, y: Int): T {
        require(x in 0 until this.width) { "X index out of bounds" }
        require(y in 0 until this.height) { "Y index out of bounds" }
        return this.internalGrid[y][x]
    }

    operator fun get(point: Point): T {
        return this[point.x.toInt(), point.y.toInt()]
    }

    fun getOrNull(x: Int, y: Int): T? {
        if (x !in 0 until this.width || y !in 0 until this.height) {
            return null
        }
        return this.internalGrid[y][x]
    }

    fun getOrNull(point: Point): T? {
        return this.getOrNull(point.x.toInt(), point.y.toInt())
    }

    fun getSubGrid(x: Int, y: Int, width: Int, height: Int): Grid<T> {
        require(x in 0 until this.width) { "X index out of bounds" }
        require(y in 0 until this.height) { "Y index out of bounds" }
        require(x + width <= this.width) { "Subgrid width out of bounds" }
        require(y + height <= this.height) { "Subgrid height out of bounds" }

        val subGrid = Grid<T>()
        for (dy in 0 until height) {
            subGrid.addRow(this.internalGrid[y + dy].subList(x, x + width))
        }
        return subGrid
    }

    operator fun set(x: Int, y: Int, value: T) {
        // Add empty rows if necessary
        if (this.internalGrid.size <= y) {
            this.internalGrid.addAll(List(y - this.internalGrid.size + 1) { mutableListOf() })
        }

        // Add empty columns if necessary
        if (this.internalGrid[y].size <= x) {
            this.internalGrid[y].addAll(List(x - this.internalGrid[y].size + 1) { null as T })
        }

        require(x in 0 until this.width) { "X index out of bounds" }
        require(y in 0 until this.height) { "Y index out of bounds" }
        this.internalGrid[y][x] = value
    }

    operator fun set(point: Point, value: T) {
        this[point.x.toInt(), point.y.toInt()] = value
    }

    inline fun <U> mapIndexed(transform: (x: Int, y: Int, T) -> U): Grid<U> {
        val newGrid = Grid<U>()
        for ((x, y, value) in this) {
            newGrid[x, y] = transform(x, y, value)
        }
        return newGrid
    }

    inline fun <U> mapIndexed(transform: (point: Point, T) -> U): Grid<U> {
        return this.mapIndexed { x, y, value -> transform(Point(x.toLong(), y.toLong()), value) }
    }

    fun toList(): List<List<T>> {
        return this.internalGrid.map { it.toList() }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Grid<*>) return false

        if (this.internalGrid != other.internalGrid) return false

        return true
    }

    fun copy(): Grid<T> {
        return Grid(this.toList().map { it.toMutableList() }.toMutableList())
    }

    override fun hashCode(): Int {
        return this.internalGrid.hashCode()
    }

    override fun toString(): String {
        return buildString {
            this.appendLine("Grid(${this@Grid.width} x ${this@Grid.height}) [")

            val strings = this@Grid.internalGrid.map { row -> row.map { it.toString() } }
            val maxElementWidth = strings.flatten().maxOfOrNull { it.length } ?: 0

            for (row in strings) {
                this.append("  [")
                for (i in row.indices) {
                    val element = row[i]
                    this.append(element.padStart(maxElementWidth))
                    if (i < row.size - 1) {
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
        fun <T> MutableList<MutableList<T>>.asGrid(): Grid<T> {
            return Grid(this)
        }

        fun <T> Iterable<Iterable<T>>.toGrid(): Grid<T> {
            val grid = Grid<T>()
            this.forEach { grid.addRow(it) }
            return grid
        }
    }
}
