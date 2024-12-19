package day19

import common.readResource

private object Part1 {
    val splitRegex = Regex("""\r?\n\r?\n""")
    val towelSplitRegex = Regex(", ")

    data class Towel(val stripes: String)

    // This is a cache to store the number of solutions for a given (sub-)design
    val designCache = mutableMapOf<String, Long>()

    fun findSolution(towels: List<Towel>, design: String): Long {
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
}

fun main() {
    val input = readResource("day19/input")

    val (inputTowels, inputDesigns) = input.split(Part1.splitRegex)

    val towels = inputTowels.split(Part1.towelSplitRegex).map {
        Part1.Towel(it)
    }

    val designs = inputDesigns.lines().filter { it.isNotEmpty() }

    val possibleDesigns = designs.count { design ->
        val solutions = Part1.findSolution(towels, design)
        println("Design: ${design}, solution: $solutions")
        solutions > 0
    }

    println(possibleDesigns)
}