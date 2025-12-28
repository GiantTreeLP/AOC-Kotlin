package year2025

import com.google.auto.service.AutoService
import common.AOCSolution
import common.Line
import common.Point
import common.readResourceLines
import java.util.function.ToLongFunction
import kotlin.math.abs
import java.lang.Long as JavaLong

@AutoService(AOCSolution::class)
class Day09 : AOCSolution {
    override val year = 2025
    override val day = 9

    override fun part1(inputFile: String): String {
        val corners = readCorners(inputFile)

        var largestRectangle = -1L
        for (i in 0 until corners.lastIndex) {
            val firstCorner = corners[i]
            for (j in i + 1 until corners.size) {
                largestRectangle = maxOf(largestRectangle, outsideArea(firstCorner, corners[j]))
            }
        }

        return largestRectangle.toString()
    }

    override fun part2(inputFile: String): String {
        val corners = readCorners(inputFile)

        val (horizontal, vertical) = buildAndPartitionLines(corners)
        // Sort the lines along their orthogonal axis
        // Horizontal lines are sorted top to bottom
        horizontal.sortBy { (from) -> from.y }
        // Vertical lines are sorted left to right
        vertical.sortBy { (from) -> from.x }

        var largestRectangleArea = -1L
        for (i in 0 until corners.lastIndex) {
            val first = corners[i]
            for (j in i + 1 until corners.size) {
                val second = corners[j]
                val area = outsideArea(first, second)
                if (area > largestRectangleArea) {
                    val topLeft = Point(minOf(first.x, second.x), minOf(first.y, second.y))
                    val bottomRight = Point(maxOf(first.x, second.x), maxOf(first.y, second.y))
                    if (intersectsNone(horizontal, vertical, topLeft, bottomRight)) {
                        largestRectangleArea = maxOf(largestRectangleArea, area)
                    }
                }
            }
        }

        return largestRectangleArea.toString()
    }

    private companion object {
        private fun readCorners(inputFile: String): List<Point> {
            val corners = readResourceLines(inputFile).map { line ->
                val splitIndex = line.indexOf(',')
                val x = JavaLong.parseLong(line, 0, splitIndex, 10)
                val y = JavaLong.parseLong(line, splitIndex + 1, line.length, 10)
                Point(x, y)
            }
            return corners
        }

        /**
         * Builds two lists of lines from connected corners in [corners].
         * The corners are required to construct lines that are orthogonal to their
         * predecessors and successors.
         *
         * @return two lists, the first one containing the horizontal lines and
         * the second one containing the vertical lines.
         */
        private fun buildAndPartitionLines(corners: List<Point>): Pair<Array<Line>, Array<Line>> {
            require(corners.size >= 2)

            // Origin point of the next line
            var from = corners.last()
            val lines = buildList {
                corners.forEach { to ->
                    add(Line(from, to))
                    from = to
                }
            }

            val (horizontal, vertical) = lines.partition { line -> line.from.y == line.to.y }
            return horizontal.toTypedArray() to vertical.toTypedArray()
        }

        private fun outsideArea(a: Point, b: Point): Long {
            val width = abs(a.x - b.x) + 1
            val height = abs(a.y - b.y) + 1
            return width * height
        }

        /**
         * Checks whether a rectangle intersects any line in the arrays of horizontal and vertical lines.
         *
         * @param horizontal the array of strictly horizontal lines
         * @param vertical the array of strictly vertical lines
         * @param topLeft the top-left corner of the rectangle to check
         * @param bottomRight the bottom-right corner of the rectangle to check
         * @return whether any of the lines intersect the rectangle
         */
        private fun intersectsNone(
            horizontal: Array<Line>,
            vertical: Array<Line>,
            topLeft: Point,
            bottomRight: Point
        ): Boolean {
            // Find the index of the first horizontal line above the rectangle
            val beginHorizontal = horizontal.binarySearchFirst(
                target = topLeft.y
            ) { (from) -> from.y }.coerceAtLeast(0)

            for (i in beginHorizontal until horizontal.size) {
                if (horizontal[i].from.y >= bottomRight.y) {
                    // End early, when encountering the first line on the bottom edge
                    break
                }
                if (isLineInsideRect(horizontal[i], topLeft, bottomRight)) {
                    return false
                }
            }

            // Find the index of the first vertical line on the left edge of the rectangle
            val beginVertical = vertical.binarySearchFirst(
                target = topLeft.x
            ) { (from) -> from.x }.coerceAtLeast(0)

            for (i in beginVertical until vertical.size) {
                if (vertical[i].from.x >= bottomRight.x) {
                    // End early, when encountering the first line on the right edge
                    break
                }
                if (isLineInsideRect(vertical[i], topLeft, bottomRight)) {
                    return false
                }
            }

            return true
        }

        /**
         * Axis-aligned line to rectangle intersection test.
         * Lines are considered inside, if and only if, they are inside the horizontal
         * or vertical bounds of the rectangle.
         * They are not considered inside, if they are on the edges of the rectangle.
         * @param line the line to check the intersection for
         * @param topLeft the top-left corner of the rectangle to check
         * @param bottomRight the bottom-right corner of the rectangle to check
         * @return whether the line intersects the rectangle
         */
        private fun isLineInsideRect(
            line: Line,
            topLeft: Point,
            bottomRight: Point
        ): Boolean {
            val (left, top) = topLeft
            val (right, bottom) = bottomRight

            val fromX = line.from.x
            val toX = line.to.x

            // Vertical
            if ((fromX <= left && toX <= left) || (fromX >= right && toX >= right)) {
                // Line is completely to the left or right of the rectangle
                return false
            }

            val fromY = line.from.y
            val toY = line.to.y
            // Horizontal
            if ((fromY <= top && toY <= top) || (fromY >= bottom && toY >= bottom)) {
                // Line is completely above or below the rectangle
                return false
            }
            return true
        }

        private fun <T> Array<T>.binarySearchFirst(
            from: Int = 0,
            to: Int = size,
            target: Long,
            extractor: ToLongFunction<T>
        ): Int {
            var low = from
            var high = to - 1
            var result = -1

            while (low <= high) {
                val mid = low + ((high - low) / 2)
                val comparison = extractor.applyAsLong(get(mid)).compareTo(target)
                if (comparison == 0) {
                    result = mid
                    high = mid - 1
                } else if (comparison < 0) {
                    low = mid + 1
                } else {
                    high = mid - 1
                }
            }
            return result
        }
    }
}
