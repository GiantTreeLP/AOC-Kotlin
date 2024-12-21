package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceTwoParts

@AutoService(AOCSolution::class)
class Day19 : AOCSolution {
    override val year = 2024
    override val day = 19

    private data class Towel(val stripes: String)

    // This is a cache to store the number of solutions for a given (sub-)design
    private val designCache = mutableMapOf<String, Long>()

    private fun findSolution(towels: List<Towel>, design: String): Long {
        designCache[design]?.let {
            return it
        }

        return if (design.isEmpty()) {
            // We have a solution
            1
        } else {
            towels.sumOf { (stripes) ->
                if (design.startsWith(stripes)) {
                    val remainingDesign = design.substring(stripes.length)
                    findSolution(towels, remainingDesign)
                } else {
                    0
                }
            }
        }.also {
            designCache[design] = it
        }
    }

    override fun part1(inputFile: String): String {
        designCache.clear()
        val (inputTowels, inputDesigns) = readResourceTwoParts(inputFile)

        val towels = inputTowels.split(", ").map {
            Towel(it)
        }

        val designs = inputDesigns.lines().filter { it.isNotEmpty() }

        val possibleDesigns = designs.count { design ->
            val solutions = findSolution(towels, design)
            solutions > 0
        }

        return possibleDesigns.toString()
    }

    override fun part2(inputFile: String): String {
        designCache.clear()
        val (inputTowels, inputDesigns) = readResourceTwoParts(inputFile)

        val towels = inputTowels.split(", ").map {
            Towel(it)
        }

        val designs = inputDesigns.lines().filter { it.isNotEmpty() }

        val possibleDesigns = designs.sumOf { design ->
            val solutions = findSolution(towels, design)
            solutions
        }

        return possibleDesigns.toString()
    }
}