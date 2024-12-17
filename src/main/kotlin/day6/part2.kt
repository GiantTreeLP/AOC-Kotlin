package day6

import common.AdjacencyList
import common.Vertex
import common.readResourceLines
import java.util.EnumMap

private const val GUARD = '^'
private const val OBSTRUCTION_OLD = '#'
private const val OBSTRUCTION_NEW = 'O'

private const val FREE = '.'
private const val VISITED = 'X'

private object Part2 {

    enum class Direction {
        NORTH, EAST, SOUTH, WEST;

        fun turnRight(): Direction {
            return when (this) {
                NORTH -> EAST
                EAST -> SOUTH
                SOUTH -> WEST
                WEST -> NORTH
            }
        }
    }

    data class State(var x: Int, var y: Int, var direction: Direction) {
        fun moveForward() {
            when (direction) {
                Direction.NORTH -> y--
                Direction.EAST -> x++
                Direction.SOUTH -> y++
                Direction.WEST -> x--
            }
        }

        fun turnRight() {
            direction = direction.turnRight()
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || javaClass != other.javaClass) return false

            other as State

            if (x != other.x) return false
            if (y != other.y) return false
            if (direction != other.direction) return false

            return true
        }

        override fun hashCode(): Int {
            var result = x
            result = 31 * result + y
            result = 31 * result + direction.hashCode()
            return result
        }
    }
}

fun main() {
    val originalMap = readResourceLines("day6/input")
        .map { it.toCharArray() }


    val cycles = mutableListOf<Set<Vertex<Part2.State>>>()

    for (y in originalMap.indices) {
        for (x in originalMap[y].indices) {
            val map = originalMap.map { it.copyOf() }
            if (map[y][x] == FREE) {
                map[y][x] = OBSTRUCTION_NEW
            } else {
                continue
            }
            // Find the guard's starting position
            // The direction is always north
            val startState = findGuardPosition(map)
            val state = startState.copy()

            val adjacencyList = AdjacencyList<Part2.State>()
            val vertices: List<List<EnumMap<Part2.Direction, Vertex<Part2.State>>>> = map.mapIndexed { y, line ->
                line.mapIndexed { x, _ ->
                    EnumMap<Part2.Direction, Vertex<Part2.State>>(Part2.Direction::class.java).apply {
                        Part2.Direction.entries.forEach { direction ->
                            put(direction, adjacencyList.createVertex(Part2.State(x, y, direction)))
                        }
                    }
                }
            }
            val startVertex = vertices[startState.y][startState.x][startState.direction]!!

            var lastTurn = vertices[state.y][state.x][state.direction]!!

            // We either encounter a cycle or we exit the map
            while (state.y in map.indices && state.x in map[0].indices) {
                val nextState = state.copy().apply(Part2.State::moveForward)

                if (nextState.y !in map.indices || nextState.x !in map[0].indices) {
                    state.moveForward()
                    break
                }

                when (map[nextState.y][nextState.x]) {
                    // Free
                    FREE, VISITED, GUARD -> {
                        state.moveForward()
                    }
                    // Obstruction
                    OBSTRUCTION_OLD, OBSTRUCTION_NEW -> {
                        state.turnRight()
                        val v = vertices[state.y][state.x][state.direction]!!
                        adjacencyList.addDirectedEdge(lastTurn, v)
                        lastTurn = v
                        // Check for circular path
                        val cycle = adjacencyList.getCycle(startVertex)
                        if (cycle != null) {
                            cycles.add(cycle)
                            break
                        }
                    }
                }
            }
        }
    }

    cycles.forEach { printTurns(it) }
    println("Cycles: ${cycles.size}")
}

private fun printTurns(vertices: Set<Vertex<Part2.State>>) {
    println(vertices.map(Vertex<Part2.State>::data).joinToString(" -> ") {
        "(${it.x}, ${it.y}, ${it.direction})"
    })
}

private fun findGuardPosition(input: List<CharArray>): Part2.State {
    input.forEachIndexed { index, line ->
        val guardIndex = line.indexOf(GUARD)
        if (guardIndex != -1) {
            return Part2.State(guardIndex, index, Part2.Direction.NORTH)
        }
    }
    throw IllegalArgumentException("No guard found")
}
