package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResource

@AutoService(AOCSolution::class)
class Day11 : AOCSolution {
    override val year = 2024
    override val day = 11

    private val cache = mutableMapOf<Pair<Long, Int>, Long>()

    private fun applyRule(stone: Long): List<Long> {
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

    private fun countStones(stone: Long, remaining: Int): Long {
        if (remaining == 0) {
            return 1
        }

        val key = Pair(stone, remaining)
        if (key in cache) {
            return cache[key]!!
        }

        val result = applyRule(stone).sumOf { countStones(it, remaining - 1) }
        cache[key] = result
        return result
    }

    override fun part1(inputFile: String): String {
        val input = readResource(inputFile).trim()
        val stones = input.split(" ").map { it.toLong() }

        return (stones.sumOf { countStones(it, 25) }).toString()
    }

    override fun part2(inputFile: String): String {
        val input = readResource(inputFile).trim()
        val stones = input.split(" ").map { it.toLong() }

        return (stones.sumOf { countStones(it, 75) }).toString()
    }
}