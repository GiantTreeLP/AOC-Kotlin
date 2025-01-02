package common.grid

import common.Point
import common.Rectangle

class TransposedGrid<T : Any>(private val grid: Grid<T>) : Grid<T>, Iterable<T> {
    override val width = this.grid.height
    override val height = this.grid.width
    override val bounds = Rectangle(Point(0, 0), Point(this.width, this.height))
    override val indices = PointProgression(width.toLong(), height.toLong(), 1)

    @Suppress("UNCHECKED_CAST")
    override val values: Array<T>
        get() = Array<Any?>(this.width * this.height) { index ->
            val x = index % this.width
            val y = index / this.width
            this.grid[y, x]
        } as Array<T>

    override operator fun get(x: Int, y: Int): T {
        return this.grid[y, x]
    }

    override operator fun get(point: Point): T {
        return this[point.x.toInt(), point.y.toInt()]
    }

    override fun get(row: Int, arrayType: Class<T>): Array<T> {
        require(row in 0 until this.width) { "Row index out of bounds" }
        @Suppress("UNCHECKED_CAST")
        return (java.lang.reflect.Array.newInstance(arrayType, this.height) as Array<T>)
            .apply {
                for (y in indices) {
                    this[y] = this@TransposedGrid.grid[row, y]
                }
            }
    }

    override fun getOrNull(x: Int, y: Int): T? {
        return this.grid.getOrNull(y, x)
    }

    override fun getOrNull(point: Point): T? {
        return this.getOrNull(point.x.toInt(), point.y.toInt())
    }

    override fun transposed(): Grid<T> {
        return this.grid
    }

    override operator fun set(x: Int, y: Int, value: T) {
        this.grid[y, x] = value
    }

    override operator fun set(point: Point, value: T) {
        this[point.x.toInt(), point.y.toInt()] = value
    }

    override fun set(row: Int, value: Array<T>) {
        require(row in 0 until this.width) { "Row index out of bounds" }
        require(value.size == this.height) { "Row size does not match grid width" }
        for (y in 0 until this.height) {
            this.grid[y, row] = value[y]
        }
    }
}