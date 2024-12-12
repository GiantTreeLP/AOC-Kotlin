package day12

import common.Grid
import common.Grid.Companion.toGrid
import common.Point
import common.readResourceLines
import day12.Part2.findRegionSides

private object Part2 {
    data class PlantRegion(val plant: Char, val plants: Set<Point>)

    fun neighbours(point: Point, grid: Grid<Char>): List<Pair<Point, Char>> {
        return point.neighbours()
            .map { point -> point to grid.getOrNull(point) }
            .filter { (_, c) -> c != null }
            .map { (p, c) -> p to c!! }
    }

    fun findAllRegions(grid: Grid<Char>): List<PlantRegion> {
        val visited = mutableSetOf<Pair<Point, Char>>()
        val regions = mutableListOf<PlantRegion>()

        for (cell in grid.pointIterator()) {
            if (cell !in visited) {
                val plants = mutableSetOf(cell.first)
                val stack = mutableListOf(cell)

                while (stack.isNotEmpty()) {
                    val current = stack.removeLast()
                    visited.add(current)

                    val currentNeighbours = neighbours(current.first, grid)
                    val next = currentNeighbours.filter { it.second == current.second && it !in visited }
                    stack.addAll(next)
                    plants.addAll(next.map { it.first })
                }

                regions.add(PlantRegion(cell.second, plants))
            }
        }

        return regions
    }

    fun findRegionEdges(region: PlantRegion): List<Pair<Point, Point>> {
        return buildList {
            for (point in region.plants) {
                for (direction in Point.Directions.ALL) {
                    val neighbour = point + direction
                    if (neighbour !in region.plants) {
                        add(direction to point)
                    }
                }
            }
        }
    }

    fun findRegionSides(region: PlantRegion): Long {
        val edges = findRegionEdges(region)

        val horizontalDirections = listOf(Point.UP, Point.DOWN)
        val verticalDirections = listOf(Point.LEFT, Point.RIGHT)

        val horizontalEdges = edges
            .filter { (direction, _) -> direction in horizontalDirections }
            .groupByTo(mutableMapOf(), { it.first to it.second.y }) { it.second }
            .map {
                it.value
                    .distinct()
                    .sortedWith(Comparator.comparing { it.x })
            }
            .sumOf {
                it.windowed(2).sumOf { window ->
                    when {
                        window.size == 1 -> 0L
                        window[0].x + 1 != window[1].x -> 1L
                        else -> 0L
                    }
                } + 1
            }

        val verticalEdges = edges
            .filter { (direction, _) -> direction in verticalDirections }
            .groupByTo(mutableMapOf(), { it.first to it.second.x }) { it.second }
            .map {
                it.value
                    .distinct()
                    .sortedWith(Comparator.comparing { it.y })
            }
            .sumOf {
                it.windowed(2).sumOf { window ->
                    when {
                        window.size == 1 -> 0L
                        window[0].y + 1 != window[1].y -> 1L
                        else -> 0L
                    }
                } + 1
            }

        return horizontalEdges + verticalEdges
    }
}

fun main() {
    val grid = readResourceLines("day12/input").map { it.toCharArray().toList() }.toGrid()

//    println(grid)

    // Build the plant regions
    val regions = Part2.findAllRegions(grid)

    val regionsPrice = regions.sumOf { region ->
        // Find the amount of sides of the region and multiply it by the amount of plants in the region
        // yielding the price of the region
        findRegionSides(region) * region.plants.size
    }

    println(regionsPrice)
}
