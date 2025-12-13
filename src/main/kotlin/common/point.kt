package common

import kotlin.math.abs

typealias Position = Point
typealias Direction = Point

@JvmRecord
data class Point(val x: Long, val y: Long) {
    val manhattanDistance get() = abs(x) + abs(y)

    constructor(x: Int, y: Int) : this(x.toLong(), y.toLong())

    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun minus(other: Point) = Point(x - other.x, y - other.y)

    operator fun times(scalar: Long): Point {
        return Point(x * scalar, y * scalar)
    }

    infix operator fun rangeTo(other: Point): Rectangle {
        val topLeft = Point(minOf(x, other.x), minOf(y, other.y))
        val bottomRight = Point(maxOf(x, other.x), maxOf(y, other.y))
        return Rectangle(topLeft, bottomRight)
    }

    fun turnRight() = Point(-y, x)
    fun turnLeft() = Point(y, -x)

    companion object Directions {
        val UP = Point(0, -1)
        val DOWN = Point(0, 1)
        val LEFT = Point(-1, 0)
        val RIGHT = Point(1, 0)

        val ALL = listOf(UP, DOWN, LEFT, RIGHT)
    }

    fun neighbours() = ALL.map { this + it }
}

data class Rectangle(val topLeft: Point, val bottomRight: Point) {
    val width = bottomRight.x - topLeft.x
    val height = bottomRight.y - topLeft.y

    infix operator fun contains(point: Point) =
        point.x in topLeft.x..<bottomRight.x && point.y in topLeft.y..<bottomRight.y
}
