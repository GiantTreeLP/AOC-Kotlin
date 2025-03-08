package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceLines

@AutoService(AOCSolution::class)
class Day18 : AOCSolution {
    override val year = 2015
    override val day = 18

    private fun gameOfLife(grid: BooleanArray, stride: Int): BooleanArray {
        return BooleanArray(grid.size) { index ->
            val x = index % stride
            val y = index / stride
            val neighbours = grid.neighboursCount(x, y, stride)
            if (grid[index]) {
                neighbours == 2 || neighbours == 3
            } else {
                neighbours == 3
            }
        }
    }

    private fun turnOnCorners(grid: BooleanArray, stride: Int) {
        // Top left
        grid[0] = true

        // Top right
        grid[stride - 1] = true

        // Bottom left
        grid[stride * (stride - 1)] = true

        // Bottom right
        grid[grid.size - 1] = true
    }

    private fun readInput(inputFile: String): Pair<Int, BooleanArray> {
        val lines = readResourceLines(inputFile)
        val stride = lines.size

        var grid = BooleanArray(stride * stride) {
            val x = it % stride
            val y = it / stride
            lines[y][x] == '#'
        }
        return Pair(stride, grid)
    }

    override fun part1(inputFile: String): String {
        val steps = if (inputFile.endsWith("sample")) SAMPLE_STEPS_PART1 else INPUT_STEPS

        val pair = readInput(inputFile)
        val stride = pair.first
        var grid = pair.second

        repeat(steps) {
            grid = gameOfLife(grid, stride)
        }

        return grid.count { it }.toString()
    }

    override fun part2(inputFile: String): String {
        val steps = if (inputFile.endsWith("sample")) SAMPLE_STEPS_PART2 else INPUT_STEPS

        val pair = readInput(inputFile)
        val stride = pair.first
        var grid = pair.second

        turnOnCorners(grid, stride)

        repeat(steps) {
            grid = gameOfLife(grid, stride)
            turnOnCorners(grid, stride)
        }

        return grid.count { it }.toString()
    }

    companion object {
        private const val SAMPLE_STEPS_PART1 = 4
        private const val SAMPLE_STEPS_PART2 = 5
        private const val INPUT_STEPS = 100

        private fun BooleanArray.neighboursCount(x: Int, y: Int, stride: Int): Int {
            var result = 0

            for (dx in -1..1) {
                for (dy in -1..1) {
                    if (dx == 0 && dy == 0) {
                        continue
                    }
                    val nx = x + dx
                    val ny = y + dy

                    if (nx < 0 || nx >= stride || ny < 0 || ny >= stride) {
                        continue
                    }
                    if (this[ny * stride + nx]) {
                        result++
                    }
                }
            }
            return result
        }
    }
}
