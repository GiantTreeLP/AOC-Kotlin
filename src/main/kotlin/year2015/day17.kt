package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceLines

@AutoService(AOCSolution::class)
class Day17 : AOCSolution {
    override val year = 2015
    override val day = 17

    /**
     * Count the number of combinations of containers that can hold the given amount of eggnog.
     * @param remaining The amount of eggnog that still needs to be stored.
     * @param index The index of the current container.
     * @param containers The list of containers (their sizes).
     * @param containersAmount The amount of containers used so far.
     * @param solutionsAmounts The map of solutions amounts for each amount of containers used.
     */
    private fun countCombinations(
        remaining: Int,
        index: Int = 0,
        containers: IntArray,
        containersAmount: Int = 0,
        solutionsAmounts: MutableMap<Int, Int> = mutableMapOf()
    ): Int {
        return if (remaining == 0) {
            // Add the solution to the map
            solutionsAmounts.merge(containersAmount, 1) { a, b -> a + b }
            1
        } else if (remaining < 0 || index >= containers.size) {
            0
        } else {
            // Combinations with the current container and without it
            countCombinations(
                remaining - containers[index],
                index + 1,
                containers,
                containersAmount + 1,
                solutionsAmounts
            ) +
                    countCombinations(
                        remaining,
                        index + 1,
                        containers,
                        containersAmount,
                        solutionsAmounts
                    )
        }
    }

    override fun part1(inputFile: String): String {
        val containers = readResourceLines(inputFile)
            .map { it.toInt() }
            .toIntArray()

        val eggnog = if (inputFile.endsWith("sample")) {
            EGGNOG_SAMPLE
        } else {
            EGGNOG_ACTUAL
        }

        return countCombinations(eggnog, containers = containers).toString()
    }

    override fun part2(inputFile: String): String {
        val containers = readResourceLines(inputFile)
            .map { it.toInt() }
            .toIntArray()

        val eggnog = if (inputFile.endsWith("sample")) {
            EGGNOG_SAMPLE
        } else {
            EGGNOG_ACTUAL
        }

        val solutionsAmounts = mutableMapOf<Int, Int>()
        countCombinations(eggnog, containers = containers, solutionsAmounts = solutionsAmounts)

        val minContainers = solutionsAmounts.minBy { it.key }
        return solutionsAmounts[minContainers.key].toString()
    }

    companion object {
        const val EGGNOG_SAMPLE = 25
        const val EGGNOG_ACTUAL = 150
    }
}
