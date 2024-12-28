package year2024

import com.google.auto.service.AutoService
import common.*
import common.grid.Grid
import common.grid.toGrid

@AutoService(AOCSolution::class)
class Day06 : AOCSolution {
    override val year = 2024
    override val day = 6

    private data class State(var x: Int, var y: Int, var direction: Direction) {
        fun moveForward() {
            x += direction.x.toInt()
            y += direction.y.toInt()
        }

        fun turnRight() {
            direction = direction.turnRight()
        }
    }

    private fun findGuardPosition(map: Grid<Char>): State {
        for (p in map.indices) {
            if (map[p] == GUARD) {
                val (x, y) = p
                return State(x.toInt(), y.toInt(), Direction.UP)
            }
        }
        error("Guard not found")
    }

    private fun simulateGuard(startState: State, map: Grid<Char>) {
        val state = startState.copy()
        val bounds = map.bounds

        while (state.x in 0 until bounds.width && state.y in 0 until bounds.height) {
            val nextState = state.copy().apply(State::moveForward)

            if (nextState.x !in 0 until bounds.width || nextState.y !in 0 until bounds.height) {
                map[state.x, state.y] = VISITED
                break
            }

            when (map[nextState.x, nextState.y]) {
                // Free
                FREE, VISITED -> {
                    map[state.x, state.y] = VISITED
                    state.moveForward()
                }
                // Obstruction
                OBSTRUCTION_OLD -> {
                    state.turnRight()
                }
            }
        }
    }

    override fun part1(inputFile: String): String {
        val map = readResourceLines(inputFile)
            .map { it.toList() }
            .toGrid()

        // Find the guard's starting position
        // The direction is always north
        val state = findGuardPosition(map)

        simulateGuard(state, map)

        val result = map.count { c -> c == VISITED }

        return result.toString()
    }

    override fun part2(inputFile: String): String {
        val map = readResourceLines(inputFile)
            .map { it.toList() }
            .toGrid()

        // Find the guard's starting position
        // The direction is always north
        val startState = findGuardPosition(map)

        // Fill the map with the guard's path
        simulateGuard(startState, map)
        // Restore the guard at the starting position
        map[startState.x, startState.y] = GUARD

        var cycles = 0
        val bounds = map.bounds
        val history = mutableListOf<State>()

        // This approach is not efficient, but it works
        map
            // Put an obstruction on each cell the guard has visited
            .filter { cell -> cell == VISITED }
            .forEachIndexed { index, c ->
                val x = (index % bounds.width).toInt()
                val y = (index / bounds.width).toInt()
                // Save the original cell
                val oldChar = c
                map[x, y] = OBSTRUCTION_NEW

                val state = startState.copy()
                history.clear()

                // We either encounter a cycle or we exit the map
                while (state.x in 0 until bounds.width && state.y in 0 until bounds.height) {
                    val nextState = state.copy().apply(State::moveForward)

                    when (map.getOrNull(nextState.x, nextState.y)) {
                        // Free
                        FREE, VISITED, GUARD -> {
                            state.moveForward()
                        }
                        // Obstruction
                        OBSTRUCTION_OLD, OBSTRUCTION_NEW -> {
                            state.turnRight()
                            history.addLast(state.copy())
                            if (history.indexOf(state) < history.lastIndex) {
                                cycles++
                                break
                            }
                        }

                        null -> {
                            break
                        }
                    }
                }

                // Restore the original cell
                map[x, y] = oldChar
            }

        return cycles.toString()
    }

    companion object {
        private const val GUARD = '^'
        private const val OBSTRUCTION_OLD = '#'
        private const val OBSTRUCTION_NEW = 'O'

        private const val FREE = '.'
        private const val VISITED = 'X'
    }
}