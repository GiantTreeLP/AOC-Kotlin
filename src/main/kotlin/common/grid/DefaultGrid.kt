package common.grid

import common.Point
import common.Rectangle

class DefaultGrid<T : Any>(
    override val width: Int,
    override val height: Int,
    grid: Array<T>
) : Grid<T> {
    private val internalGrid: Array<T> = grid

    override val bounds = Rectangle(Point(0, 0), Point(this.width, this.height))

    override val values get() = this.internalGrid

    override val indices = PointProgression(width.toLong(), height.toLong(), 1)

    @Suppress("UNCHECKED_CAST")
    constructor(width: Int, height: Int, initializer: (x: Int, y: Int) -> T) : this(
        width,
        height,
        (java.lang.reflect.Array.newInstance(
            Any::class.java,
            width * height
        ) as Array<T>).apply {
            this.indices.forEach { index ->
                this[index] = initializer(index % width, index / width)
            }
        }
    )

    constructor(width: Int, height: Int, initializer: (Point) -> T) : this(width, height, { x, y ->
        initializer(Point(x, y))
    })

    override operator fun get(x: Int, y: Int): T {
        require(x in 0 until this.width) { "X index out of bounds" }
        require(y in 0 until this.height) { "Y index out of bounds" }
        return this.internalGrid[y * this.width + x]
    }

    override operator fun get(point: Point): T {
        return this[point.x.toInt(), point.y.toInt()]
    }

    override fun get(row: Int, arrayType: Class<T>): Array<T> {
        require(row in 0 until this.width) { "X index out of bounds" }
        return this.internalGrid.copyOfRange(row * this.width, (row + 1) * this.width)
    }

    override fun getOrNull(x: Int, y: Int): T? {
        if (x !in 0 until this.width || y !in 0 until this.height) {
            return null
        }
        return this.internalGrid[y * this.width + x]
    }

    override fun getOrNull(point: Point): T? {
        return this.getOrNull(point.x.toInt(), point.y.toInt())
    }

    override fun transposed(): Grid<T> {
        return TransposedGrid(this)
    }

    override operator fun set(x: Int, y: Int, value: T) {
        require(x in 0 until this.width) { "X index out of bounds" }
        require(y in 0 until this.height) { "Y index out of bounds" }
        this.internalGrid[y * this.width + x] = value
    }

    override operator fun set(point: Point, value: T) {
        this[point.x.toInt(), point.y.toInt()] = value
    }

    override fun set(row: Int, value: Array<T>) {
        require(row in 0 until this.height) { "Row index out of bounds" }
        require(value.size == this.width) { "Row size does not match grid width" }
        value.copyInto(this.internalGrid, row * this.width)
    }

    fun toList(): List<List<T>> {
        return this.internalGrid.asList().chunked(this.width)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DefaultGrid<*>) return false

        if (!this.internalGrid.contentEquals(other.internalGrid)) return false

        return true
    }

    override fun hashCode(): Int {
        return this.internalGrid.contentHashCode()
    }

    override fun toString(): String {
        return buildString {
            this.appendLine("Grid(${this@DefaultGrid.width} x ${this@DefaultGrid.height}) [")

            val strings = this@DefaultGrid.internalGrid.map { it.toString() }
            val maxElementWidth = strings.maxOfOrNull { it.length } ?: 0

            for (rowIndex in 0 until height) {
                this.append("  [")
                for (i in 0 until width) {
                    val element = strings[rowIndex * width + i]
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

    companion object {
        inline fun <reified T : Any> filled(width: Int, height: Int, value: T): DefaultGrid<T> {
            val gridArray = Array(width * height) { value }
            return DefaultGrid(width, height, gridArray)
        }
    }
}