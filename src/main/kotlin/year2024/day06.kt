package year2024

import com.google.auto.service.AutoService
import common.*
import common.Grid.Companion.toGrid

@AutoService(AOCSolution::class)
class Day06 : AOCSolution {
    override val year = 2024
    override val day = 6

    private data class State(var position: Position, var direction: Direction) {
        fun moveForward() {
            position += direction
        }

        fun turnRight() {
            direction = direction.turnRight()
        }
    }

    private fun findGuardPosition(map: Grid<Char>): State {
        map.first { (_, _, cell) -> cell == GUARD }
            .let { (x, y, _) -> return State(Position(x, y), Direction.UP) }
    }

    override fun part1(inputFile: String): String {
        val map = readResourceLines(inputFile)
            .map { it.toList() }
            .toGrid()

        // Find the guard's starting position
        // The direction is always north
        val state = findGuardPosition(map)

        while (state.position in map.bounds) {
            val nextState = state.copy().apply(State::moveForward)

            if (nextState.position !in map.bounds) {
                map[state.position] = VISITED
                state.moveForward()
                break
            }

            when (map[nextState.position]) {
                // Free
                FREE, VISITED -> {
                    map[state.position] = VISITED
                    state.moveForward()
                }
                // Obstruction
                OBSTRUCTION_OLD -> {
                    state.turnRight()
                }
            }
        }

        val result = map.count { (_, _, c) -> c == VISITED }

        return result.toString()
    }

    override fun part2(inputFile: String): String {
        val originalMap = readResourceLines(inputFile)
            .map { it.toList() }
            .toGrid()

        val cycles = mutableListOf<Set<Vertex<State>>>()

        // Try putting an obstruction on every free cell
        // This approach is not efficient, but it works
        originalMap.forEach { (x, y, _) ->
            if (originalMap[x, y] != FREE) {
                return@forEach
            }
            val map = originalMap.copy()
            map[x, y] = OBSTRUCTION_NEW

            // Find the guard's starting position
            // The direction is always north
            val startState = findGuardPosition(map)
            val state = startState.copy()

            val adjacencyList = AdjacencyList<State>()
            val vertices = Grid(map.width, map.height) { (x, y) ->
                buildMap {
                    Direction.ALL.forEach { direction: Direction ->
                        put(direction, adjacencyList.createVertex(State(Position(x, y), direction)))
                    }
                }
            }
            val startVertex = vertices[startState.position][startState.direction]!!

            var lastTurn = vertices[state.position][state.direction]!!

            // We either encounter a cycle or we exit the map
            while (state.position in map.bounds) {
                val nextState = state.copy().apply(State::moveForward)

                when (map.getOrNull(nextState.position)) {
                    // Free
                    FREE, VISITED, GUARD -> {
                        state.moveForward()
                    }
                    // Obstruction
                    OBSTRUCTION_OLD, OBSTRUCTION_NEW -> {
                        state.turnRight()
                        val v = vertices[state.position][state.direction]!!
                        adjacencyList.addDirectedEdge(lastTurn, v)
                        lastTurn = v

                        // Check for circular path
                        val cycle = adjacencyList.getCycle(startVertex)
                        if (cycle != null) {
                            cycles.add(cycle)
                            break
                        }
                    }

                    null -> {
                        state.moveForward()
                        break
                    }
                }
            }
        }

        return cycles.size.toString()
    }

    companion object {
        private const val GUARD = '^'
        private const val OBSTRUCTION_OLD = '#'
        private const val OBSTRUCTION_NEW = 'O'

        private const val FREE = '.'
        private const val VISITED = 'X'
    }
}