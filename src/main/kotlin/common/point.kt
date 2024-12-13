package common

data class Point(val x: Long, val y: Long) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun minus(other: Point) = Point(x - other.x, y - other.y)

    companion object Directions {
        val UP = Point(0, -1)
        val DOWN = Point(0, 1)
        val LEFT = Point(-1, 0)
        val RIGHT = Point(1, 0)

        val ALL = listOf(UP, DOWN, LEFT, RIGHT)
    }

    fun neighbours() = ALL.map { this + it }
}
