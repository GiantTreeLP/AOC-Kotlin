package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceLines

@AutoService(AOCSolution::class)
class Day16 : AOCSolution {
    override val year = 2015
    override val day = 16

    data class Sue(
        val number: Int,
        val children: Int?,
        val cats: Int?,
        val samoyeds: Int?,
        val pomeranians: Int?,
        val akitas: Int?,
        val vizslas: Int?,
        val goldfish: Int?,
        val trees: Int?,
        val cars: Int?,
        val perfumes: Int?
    ) {
        fun isClose(other: Sue): Boolean {
            return (children == null || children == other.children) &&
                    (cats == null || cats == other.cats) &&
                    (samoyeds == null || samoyeds == other.samoyeds) &&
                    (pomeranians == null || pomeranians == other.pomeranians) &&
                    (akitas == null || akitas == other.akitas) &&
                    (vizslas == null || vizslas == other.vizslas) &&
                    (goldfish == null || goldfish == other.goldfish) &&
                    (trees == null || trees == other.trees) &&
                    (cars == null || cars == other.cars) &&
                    (perfumes == null || perfumes == other.perfumes)
        }

        fun isSimilar(other: Sue): Boolean {
            return (children == null || children == other.children) &&
                    (cats == null || other.cats == null || cats > other.cats) &&
                    (samoyeds == null || samoyeds == other.samoyeds) &&
                    (pomeranians == null || other.pomeranians == null || pomeranians < other.pomeranians) &&
                    (akitas == null || akitas == other.akitas) &&
                    (vizslas == null || vizslas == other.vizslas) &&
                    (goldfish == null || other.goldfish == null || goldfish < other.goldfish) &&
                    (trees == null || other.trees == null || trees > other.trees) &&
                    (cars == null || cars == other.cars) &&
                    (perfumes == null || perfumes == other.perfumes)
        }

        companion object {
            fun fromMap(number: Int, map: Map<String, Int>): Sue {
                return Sue(
                    number,
                    map["children"],
                    map["cats"],
                    map["samoyeds"],
                    map["pomeranians"],
                    map["akitas"],
                    map["vizslas"],
                    map["goldfish"],
                    map["trees"],
                    map["cars"],
                    map["perfumes"]
                )
            }
        }
    }

    private fun parseSues(inputFile: String): List<Sue> {
        return readResourceLines(inputFile).map { line ->
            val match = sueRegex.matchEntire(line) ?: error("Invalid input line: $line")

            val number = match.groupValues[1].toInt()

            val map = match.groupValues
                .drop(2)
                .chunked(2)
                .associate { (key, value) ->
                    key to value.toInt()
                }

            Sue.fromMap(number, map)
        }
    }

    override fun part1(inputFile: String): String {
        if (inputFile.endsWith("sample")) {
            return "No sample available"
        }

        val sues = parseSues(inputFile)

        val closeSue = sues.first { it.isClose(targetSue) }

        return closeSue.number.toString()
    }

    override fun part2(inputFile: String): String {
        if (inputFile.endsWith("sample")) {
            return "No sample available"
        }

        val sues = parseSues(inputFile)

        val mostSimilarSue = sues.first { it.isSimilar(targetSue) }

        return mostSimilarSue.number.toString()
    }

    companion object {
        private val sueRegex = Regex("""Sue (\d+): (\w+): (\d+), (\w+): (\d+), (\w+): (\d+)""")

        private val targetSue = Sue(
            0,
            children = 3,
            cats = 7,
            samoyeds = 2,
            pomeranians = 3,
            akitas = 0,
            vizslas = 0,
            goldfish = 5,
            trees = 3,
            cars = 2,
            perfumes = 1
        )
    }
}
