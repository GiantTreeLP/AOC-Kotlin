package common.grid

class GridIterator<T : Any>(private val grid: Grid<T>) : Iterator<T> {
    private val width = this.grid.width
    private val height = this.grid.height
    private var x = 0
    private var y = 0

    override fun next(): T {
        if (!this.hasNext()) throw NoSuchElementException()
        val value = this.grid[this.x, this.y]
        this.x++
        if (this.x >= this.width) {
            this.x = 0
            this.y++
        }
        return value
    }

    override fun hasNext(): Boolean {
        return this.x < this.width && this.y < this.height
    }

}