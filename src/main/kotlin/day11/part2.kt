package day11

import common.readResource
import day11.Part2.countStones

private object Part2 {

    fun applyRule(stone: Long): List<Long> {
        return if (stone == 0L) {
            // Rule 1: 0 -> 1
            listOf(1)
        } else {
            val stoneString = stone.toString()
            if (stoneString.length % 2 == 0) {
                // Rule 2: 1234 -> 12 34
                listOf(
                    stoneString.substring(0, stoneString.length / 2).toLong(),
                    stoneString.substring(stoneString.length / 2).toLong()
                )
            } else {
                // Rule 3: 1 -> 1*2024
                listOf(stone * 2024L)
            }
        }
    }

    fun countStones(stone: Long, remaining: Int, cache: MutableMap<Pair<Long, Int>, Long>): Long {
        if (remaining == 0) {
            return 1
        }

        val key = Pair(stone, remaining)
        if (key in cache) {
            return cache[key]!!
        }

        val result = applyRule(stone).sumOf { countStones(it, remaining - 1, cache) }
        cache[key] = result
        return result
    }

}

fun main() {
    val input = readResource("day11/input").trim()
    var stones = input.split(" ").map { it.toLong() }


    val cache = mutableMapOf<Pair<Long, Int>, Long>()
    println(stones.sumOf { countStones(it, 75, cache) })
}
