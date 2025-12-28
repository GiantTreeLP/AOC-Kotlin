package common

import common.Rectangle.Companion.Rectangle
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
        return Rectangle(this, other)
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

@ConsistentCopyVisibility
data class Rectangle private constructor(val topLeft: Point, val bottomRight: Point) {

    val width = bottomRight.x - topLeft.x
    val height = bottomRight.y - topLeft.y

    val area get() = width * height
    val outsideArea get() = (width + 1) * (height + 1)

    infix operator fun contains(point: Point) =
        point.x in topLeft.x..<bottomRight.x && point.y in topLeft.y..<bottomRight.y

    val edges: Array<Line>
        get() {
            val topRight = Point(bottomRight.x, topLeft.y)
            val bottomLeft = Point(topLeft.x, bottomRight.y)
            return arrayOf(
                Line(topLeft, topRight),
                Line(topRight, bottomRight),
                Line(bottomRight, bottomLeft),
                Line(bottomLeft, topLeft)
            )
        }

    companion object {
        fun Rectangle(first: Point, second: Point): Rectangle {
            return common.Rectangle(
                Point(minOf(first.x, second.x), minOf(first.y, second.y)),
                Point(maxOf(first.x, second.x), maxOf(first.y, second.y))
            )
        }
    }
}

@JvmRecord
data class Line(val from: Point, val to: Point) {
    fun axisAlignedRectangle() = Rectangle(from, to)
}
