package year2025

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceTwoParts

@AutoService(AOCSolution::class)
class Day05 : AOCSolution {
    override val year = 2025
    override val day = 5

    override fun part1(inputFile: String): String {
        val (freshIngredients, availableIngredients) = readResourceTwoParts(inputFile)

        val freshRanges = buildRanges(freshIngredients)

        val ingredientIds = availableIngredients.lines().filterNot(String::isEmpty).map(String::toLong)

        val count = ingredientIds.count { id -> freshRanges.any { range -> id in range } }

        return count.toString()
    }

    override fun part2(inputFile: String): String {
        val (freshIngredients, _) = readResourceTwoParts(inputFile)

        val freshRanges = buildRanges(freshIngredients)

        return freshRanges.sumOf { range ->
            range.last - range.first + 1
        }.toString()
    }

    private companion object {
        private fun buildRanges(ingredients: String): List<LongRange> {
            val lines = ingredients.lines()
            val ranges = MutableList(lines.size) { i ->
                val line = lines[i]
                val hyphen = line.indexOf('-')
                val lower = line.take(hyphen).toLong()
                val upper = line.substring(hyphen + 1).toLong()
                LongRange(lower, upper)
            }

            // Sort the ranges
            // The ones with the smallest ID and the least upper end
            // get sorted to the beginning.
            // This allows for easy merging, as overlapping ranges are always adjacent
            ranges.sortWith(Comparator { r1, r2 ->
                val first = r1.first.compareTo(r2.first)
                if (first != 0) {
                    first
                } else {
                    r1.last.compareTo(r2.last)
                }
            })

            // Merge adjacent ranges backwards, modifying the list in-place
            for (i in ranges.lastIndex downTo 1) {
                val lowerRange = ranges[i - 1]
                val upperRange = ranges[i]

                // The two ranges do overlap, because the tail of the first range
                // is in the second range.
                if (upperRange.first <= lowerRange.last) {
                    ranges[i - 1] = LongRange(
                        lowerRange.first,
                        maxOf(lowerRange.last, upperRange.last)
                    )
                    ranges.removeAt(i)
                }
            }

            return ranges
        }
    }
}
