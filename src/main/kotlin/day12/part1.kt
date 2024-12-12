package day12

import common.Grid
import common.Grid.Companion.toGrid
import common.Point
import common.readResourceLines

private object Part1 {
    data class PlantRegion(val plant: Char, val plants: Set<Pair<Point, Char>>)

    fun neighbours(point: Point, grid: Grid<Char>): List<Pair<Point, Char>> {
        return point.neighbours()
            .map { point -> point to grid.getOrNull(point) }
            .filter { (p, c) -> c != null }
            .map { (p, c) -> p to c!! }
    }

    fun neighboursWithNull(point: Point, grid: Grid<Char>): List<Pair<Point, Char?>> {
        return point.neighbours()
            .map { point -> point to grid.getOrNull(point) }
    }

    fun findAllRegions(grid: Grid<Char>): List<PlantRegion> {
        val visited = mutableSetOf<Pair<Point, Char>>()
        val regions = mutableListOf<PlantRegion>()

        for (cell in grid.pointIterator()) {
            if (cell !in visited) {
                val plants = mutableSetOf(cell)
                val stack = mutableListOf(cell)

                while (stack.isNotEmpty()) {
                    val current = stack.removeLast()
                    visited.add(current)

                    val currentNeighbours = neighbours(current.first, grid)
                    val next = currentNeighbours.filter { it.second == current.second && it !in visited }
                    stack.addAll(next)
                    plants.addAll(next)
                }

                regions.add(PlantRegion(cell.second, plants))
            }
        }

        return regions
    }
}

fun main() {
    val grid = readResourceLines("day12/input").map { it.toCharArray().toList() }.toGrid()

    // Build the plant regions
    val regions = Part1.findAllRegions(grid)

    val regionsPrice = regions.sumOf { region ->
        region.plants.sumOf { (p, c) ->
            // Get all neighbours of the plant that are not the plant itself
            // A neighbor that is outside the grid will have a null value
            Part1.neighboursWithNull(p, grid).count { (_, neighbor) -> c != neighbor }
        } * region.plants.size
    }

    println(regionsPrice)
}
