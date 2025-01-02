package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.grid.Grid
import common.grid.toGrid
import common.Point
import common.grid.pointIterator
import common.readResourceLines

@AutoService(AOCSolution::class)
class Day12 : AOCSolution {
    override val year = 2024
    override val day = 12

    private data class PlantRegion(val plant: Char, val plants: Set<Point>)

    private fun neighbours(point: Point, grid: Grid<Char>): List<Pair<Point, Char>> {
        @Suppress("UNCHECKED_CAST")
        return neighboursWithNull(point, grid)
            .filter { (_, c) -> c != null } as List<Pair<Point, Char>>
    }

    private fun neighboursWithNull(point: Point, grid: Grid<Char>): List<Pair<Point, Char?>> {
        return point.neighbours()
            .map { neighbour -> neighbour to grid.getOrNull(neighbour) }
    }

    private fun findAllRegions(grid: Grid<Char>): List<PlantRegion> {
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

    private fun findRegionEdges(region: PlantRegion): List<Pair<Point, Point>> {
        return buildList {
            for (point in region.plants) {
                for (direction in Point.ALL) {
                    val neighbour = point + direction
                    if (neighbour !in region.plants) {
                        add(direction to point)
                    }
                }
            }
        }
    }

    private fun findRegionSides(region: PlantRegion): Long {
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

    override fun part1(inputFile: String): String {
        val grid = readResourceLines(inputFile)
            .map(String::toList)
            .toGrid()

        // Build the plant regions
        val regions = findAllRegions(grid)

        val regionsPrice = regions.sumOf { (plant, plants) ->
            plants.sumOf { point ->
                // Get all neighbours of the plant that are not the plant itself
                // A neighbor that is outside the grid will have a null value
                neighboursWithNull(point, grid).count { (_, neighbor) -> plant != neighbor }
            } * plants.size
        }

        return regionsPrice.toString()
    }

    override fun part2(inputFile: String): String {
        val grid = readResourceLines(inputFile)
            .map(String::toList)
            .toGrid()

        // Build the plant regions
        val regions = findAllRegions(grid)

        val regionsPrice = regions.sumOf { region ->
            // Find the amount of sides of the region and multiply it by the amount of plants in the region
            // yielding the price of the region
            findRegionSides(region) * region.plants.size
        }

        return regionsPrice.toString()
    }
}