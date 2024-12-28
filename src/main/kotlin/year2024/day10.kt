package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.grid.Grid
import common.grid.mapIndexed
import common.grid.toGrid
import common.readResourceLines

@AutoService(AOCSolution::class)
class Day10 : AOCSolution {
    override val year = 2024
    override val day = 10

    private sealed class Cell(val x: Int, val y: Int, val value: Int) {
        class Trailhead(x: Int, y: Int, value: Int, val destinations: MutableList<Trail>) : Cell(x, y, value) {
            constructor(x: Int, y: Int, value: Int) : this(x, y, value, mutableListOf())

            override fun toString(): String {
                return "Trailhead(${this.x}, ${this.y}, ${this.value}, ${this.destinations})"
            }
        }

        class Trail(x: Int, y: Int, value: Int) : Cell(x, y, value) {
            override fun toString(): String {
                return "Trail(${this.x}, ${this.y}, ${this.value})"
            }
        }
    }

    private fun findAllPathsPart1(trailhead: Cell.Trailhead, cells: Grid<Cell>): List<Cell.Trail> {
        val stack: MutableList<Cell> = mutableListOf(trailhead)
        val visited: MutableSet<Cell> = mutableSetOf()
        val destinations = mutableListOf<Cell.Trail>()

        fun Cell.neighbours() = listOfNotNull(
            cells.getOrNull(this.x, this.y - 1),
            cells.getOrNull(this.x, this.y + 1),
            cells.getOrNull(this.x - 1, this.y),
            cells.getOrNull(this.x + 1, this.y)
        )

        while (stack.isNotEmpty()) {
            val currentCell = stack.removeLast()
            visited.add(currentCell)

            val neighbours = currentCell.neighbours().filter { it.value == currentCell.value + 1 && it !in visited }
            stack.addAll(neighbours)
            if (currentCell is Cell.Trail) {

                // A value of 9 indicates a trail end/peak
                if (currentCell.value == 9) {
                    destinations.add(currentCell)
                }
            }
        }

        return destinations
    }

    private fun findAllPathsPart2(trailhead: Cell.Trailhead, cells: Grid<Cell>): List<Cell.Trail> {
        val stack: MutableList<Cell> = mutableListOf(trailhead)
        val destinations = mutableListOf<Cell.Trail>()

        fun Cell.neighbours() = listOfNotNull(
            cells.getOrNull(this.x, this.y - 1),
            cells.getOrNull(this.x, this.y + 1),
            cells.getOrNull(this.x - 1, this.y),
            cells.getOrNull(this.x + 1, this.y)
        )

        while (stack.isNotEmpty()) {
            val currentCell = stack.removeLast()

            val neighbours = currentCell.neighbours().filter { it.value == currentCell.value + 1 }
            stack.addAll(neighbours)
            if (currentCell is Cell.Trail) {

                // A value of 9 indicates a trail end/peak
                if (currentCell.value == 9) {
                    destinations.add(currentCell)
                }
            }
        }

        return destinations
    }

    private fun parseTrails(inputFile: String): Grid<Cell> {
        val input = readResourceLines(inputFile)
        val grid = input.map { string -> string.map { it.digitToInt() } }.toGrid()

        val trails = grid.mapIndexed { x, y, i ->
            if (i == 0) {
                Cell.Trailhead(x, y, i)
            } else {
                Cell.Trail(x, y, i)
            }
        }
        return trails
    }

    override fun part1(inputFile: String): String {
        val trails = parseTrails(inputFile)

        // Find the peaks of each trailhead
        val trailheads = trails.filterIsInstance<Cell.Trailhead>()
        for (trailhead in trailheads) {
            val paths = findAllPathsPart1(trailhead, trails)
            trailhead.destinations.addAll(paths)
        }

        val peakCounts = trailheads.map { it.destinations.size }
        val peaks = peakCounts.sum()

        return peaks.toString()
    }

    override fun part2(inputFile: String): String {
        val trails = parseTrails(inputFile)

        // Find the peaks of each trailhead
        val trailheads = trails.filterIsInstance<Cell.Trailhead>()
        for (trailhead in trailheads) {
            val paths = findAllPathsPart2(trailhead, trails)
            trailhead.destinations.addAll(paths)
        }

        val peakCounts = trailheads.map { it.destinations.size }
        val peaks = peakCounts.sum()

        return peaks.toString()
    }
}