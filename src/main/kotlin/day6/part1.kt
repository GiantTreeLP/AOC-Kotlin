package day6

import common.readResourceLines

private const val GUARD = '^'

object Part1 {
    internal enum class Direction {
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

    internal data class State(var x: Int, var y: Int, var direction: Direction) {
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
    }

}


fun main() {
    val map = readResourceLines("day6/input")
        .map { it.toCharArray() }

    // Find the guard's starting position
    // The direction is always north

    val state = findGuardPosition(map)

    while (state.y in map.indices && state.x in map[0].indices) {
        val nextState = state.copy().apply(Part1.State::moveForward)

        if (nextState.y !in map.indices || nextState.x !in map[0].indices) {
            map[state.y][state.x] = 'X'
            state.moveForward()
            break
        }

        when (map[nextState.y][nextState.x]) {
            // Free
            '.', 'X' -> {
                map[state.y][state.x] = 'X'
                state.moveForward()
            }
            // Obstruction
            '#' -> {
                state.turnRight()
            }
        }
    }

    val result = map.sumOf { chars -> chars.count { it == 'X' } }

    println(result)
}

private fun findGuardPosition(input: List<CharArray>): Part1.State {
    input.forEachIndexed { index, line ->
        val guardIndex = line.indexOf(GUARD)
        if (guardIndex != -1) {
            return Part1.State(guardIndex, index, Part1.Direction.NORTH)
        }
    }
    throw IllegalArgumentException("No guard found")
}
