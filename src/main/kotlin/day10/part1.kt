package day10

import common.Grid
import common.Grid.Companion.toGrid
import common.readResourceLines
import day10.Part1.findAllPaths

private object Part1 {
    sealed class Cell(val x: Int, val y: Int, val value: Int) {
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

    fun findAllPaths(trailhead: Cell.Trailhead, cells: Grid<Cell>): List<Cell.Trail> {
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
}

fun main() {
    val input = readResourceLines("day10/input")
    val grid = input.map { string -> string.map { it.digitToInt() } }.toGrid()

    val trails = grid.mapIndexed { x, y, i ->
        if (i == 0) {
            Part1.Cell.Trailhead(x, y, i)
        } else {
            Part1.Cell.Trail(x, y, i)
        }
    }

    // Find the peaks of each trailhead
    val trailheads = trails.filter { it.third is Part1.Cell.Trailhead }.map { it.third as Part1.Cell.Trailhead }
    for (trailhead in trailheads) {
        val paths = findAllPaths(trailhead, trails)
        trailhead.destinations.addAll(paths)
    }

    val peakCounts = trailheads.map { it.destinations.size }
    val peaks = peakCounts.sum()

    println(peaks)
}
