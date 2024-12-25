package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResource
import common.transpose

@AutoService(AOCSolution::class)
class Day25 : AOCSolution {
    override val year = 2024
    override val day = 25

    private fun parseLocksAndKeys(inputFile: String): Pair<List<IntArray>, List<IntArray>> {
        val input = readResource(inputFile)
        val locks = lockRegex
            .findAll(input)
            .map {
                it
                    .value
                    .lines()
                    .map { line -> line.toList() }
                    .transpose()
                    .map { line -> line.count { c -> c == '#' } - 1 }
                    .toIntArray()
            }
            .toList()

        val keys = keyRegex
            .findAll(input)
            .map {
                it
                    .value
                    .lines()
                    .map { line -> line.toList() }
                    .transpose()
                    .map { line -> line.count { c -> c == '#' } - 1 }
                    .toIntArray()
            }
            .toList()

        return locks to keys
    }

    override fun part1(inputFile: String): String {
        val (locks, keys) = parseLocksAndKeys(inputFile)

        val matches = locks.map { lock ->
            keys.filter { key ->
                for (i in lock.indices) {
                    // Make sure the length of the key and lock do not exceed 5
                    if (lock[i] + key[i] > 5) {
                        return@filter false
                    }
                }
                true
            }
        }
            .flatten()
            .count()

        return matches.toString()
    }

    override fun part2(inputFile: String): String {
        return "Merry Christmas!"
    }

    companion object {
        val lockRegex = Regex("""#{5}(\r?\n[.#]{5}){6}""")
        val keyRegex = Regex("""([.#]{5}\r?\n){6}#{5}""")
    }
}