package common.grid

import common.Point

class PointProgression(
    private val width: Long,
    private val height: Long,
    private val stride: Long = 1
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