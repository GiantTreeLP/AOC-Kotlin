package day11

import common.readResource
import day11.Part1.applyRules

private object Part1 {
    fun applyRules(stones: List<Long>): List<Long> {

        return buildList {
            stones.forEach { stone ->
                if (stone == 0L) {
                    // Rule 1: 0 -> 1
                    add(1)
                } else {
                    val stoneString = stone.toString()
                    if (stoneString.length % 2 == 0) {
                        // Rule 2: 1234 -> 12 34
                        add(stoneString.substring(0, stoneString.length / 2).toLong())
                        add(stoneString.substring(stoneString.length / 2).toLong())
                    } else {
                        // Rule 3: 1 -> 1*2024
                        add(stone * 2024L)
                    }
                }
            }
        }
    }
}

fun main() {
    val input = readResource("day11/input").trim()
    var stones = input.split(" ").map { it.toLong() }

    repeat(25) {
        stones = applyRules(stones)
    }

    println(stones.size)
}
