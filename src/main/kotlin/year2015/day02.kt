package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceLines
import kotlin.math.min

@AutoService(AOCSolution::class)
class Day02 : AOCSolution {
    override val year = 2015
    override val day = 2

    private data class Present(val length: Int, val width: Int, val height: Int)

    override fun part1(inputFile: String): String {
        val presents = readResourceLines(inputFile).map { line ->
            val (l, w, h) = line.split("x").map { it.toInt() }
            Present(l, w, h)
        }

        var wrappingPaper = 0
        for ((length, width, height) in presents) {
            val side1 = length * width
            val side2 = width * height
            val side3 = height * length
            val smallestSide = min(min(side1, side2), side3)
            wrappingPaper += 2 * (side1 + side2 + side3) + smallestSide
        }
        return wrappingPaper.toString()
    }

    override fun part2(inputFile: String): String {
        val presents = readResourceLines(inputFile).map { line ->
            val (l, w, h) = line.split("x").map { it.toInt() }
            Present(l, w, h)
        }

        var ribbon = 0
        for ((length, width, height) in presents) {
            val perimeter1 = 2 * (length + width)
            val perimeter2 = 2 * (width + height)
            val perimeter3 = 2 * (height + length)
            val smallestPerimeter = min(min(perimeter1, perimeter2), perimeter3)
            val volume = length * width * height
            ribbon += smallestPerimeter + volume
        }

        return ribbon.toString()
    }
}