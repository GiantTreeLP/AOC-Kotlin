package year2024

import com.google.auto.service.AutoService
import common.*

@AutoService(AOCSolution::class)
class Day05 : AOCSolution {
    override val year = 2024
    override val day = 5

    private fun buildComparator(rules: String): Comparator<Int> {
        val orderings = rules
            .lineSequence()
            .map { it.split("|") }
            .map { it.map(String::toInt) }
            .pairs()
            .toMultiMap()

        return Comparator { a, b ->
            if (orderings[a]?.contains(b) == true) {
                -1
            } else if (orderings[b]?.contains(a) == true) {
                1
            } else {
                0
            }
        }
    }


    override fun part1(inputFile: String): String {
        val (rules, inputPages) = readResourceTwoParts(inputFile)

        val manuals = inputPages
            .lineSequence()
            .filter { it.isNotEmpty() }
            .map { it.split(",") }
            .map { it.map(String::toInt) }
            .toList()

        val comparator = buildComparator(rules)

        val result = manuals
            .filter { pages -> pages.isSorted(comparator) }
            // Extract the middle element of each manual
            .sumOf { it[it.indices.last / 2] }

        return result.toString()
    }

    override fun part2(inputFile: String): String {
        val (rules, inputPages) = readResourceTwoParts(inputFile)

        val manuals = inputPages
            .lineSequence()
            .filter { it.isNotEmpty() }
            .map { it.split(",") }
            .map { it.map(String::toInt) }
            .toList()

        val comparator = buildComparator(rules)

        val result = manuals
            .filterNot { it.isSorted(comparator) }
            // Fix the incorrectly-ordered manuals
            .map { it.sortedWith(comparator) }
            // Extract the middle element of each manual
            .sumOf { it[it.indices.last / 2] }

        return result.toString()
    }
}