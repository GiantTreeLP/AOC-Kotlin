package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.IndexPermutationIterator
import common.readResourceLines

@AutoService(AOCSolution::class)
class Day13 : AOCSolution {
    override val year = 2015
    override val day = 13

    private data class IntPair(val first: Int, val second: Int)

    private fun parsePersons(inputFile: String): Pair<MutableMap<String, Int>, MutableMap<IntPair, Int>> {
        val personIds = mutableMapOf<String, Int>()

        val persons = mutableMapOf<IntPair, Int>()
        for (line in readResourceLines(inputFile)) {
            val match = happinessRegex.matchEntire(line) ?: error("Invalid input: $line")
            var (person1, gainOrLose, happiness, person2) = match.destructured

            val gain = when (gainOrLose) {
                "lose" -> {
                    -1
                }

                else -> {
                    1
                }
            }
            val person1Index = personIds.putIfAbsent(person1, personIds.size) ?: personIds.getValue(person1)
            val person2Index = personIds.putIfAbsent(person2, personIds.size) ?: personIds.getValue(person2)
            persons.put(person1Index to person2Index, happiness.toInt() * gain)
        }

        return personIds to persons
    }

    private fun createFastPersons(personIds: Map<String, Int>, persons: Map<IntPair, Int>): IntArray {
        return IntArray(personIds.size * personIds.size) { pairIndex ->
            val person1Index = pairIndex / personIds.size
            val person2Index = pairIndex % personIds.size
            if (person1Index == person2Index) {
                0
            } else {
                persons.getValue(person1Index to person2Index)
            }
        }
    }

    private fun calculateMaxHappiness(
        personIds: MutableMap<String, Int>,
        persons: MutableMap<IntPair, Int>
    ): Int {
        val fastPersons = createFastPersons(personIds, persons)
        val stride = personIds.size

        var maxHappiness = Int.MIN_VALUE

        IndexPermutationIterator(personIds.size).forEach { seating ->
            var happiness = 0
            for (i in seating.indices) {
                val person1 = seating[i]
                val person2 = if (i + 1 < seating.size) {
                    seating[i + 1]
                } else {
                    seating[0]
                }
                happiness += fastPersons[person1 * stride + person2]
                happiness += fastPersons[person2 * stride + person1]
            }
            maxHappiness = maxOf(maxHappiness, happiness)
        }
        return maxHappiness
    }

    override fun part1(inputFile: String): String {
        val (personIds, persons) = parsePersons(inputFile)

        val maxHappiness = calculateMaxHappiness(personIds, persons)

        return maxHappiness.toString()
    }

    override fun part2(inputFile: String): String {
        val (personIds, persons) = parsePersons(inputFile)

        val me = personIds.size
        personIds["me"] = me

        for (i in 0 until me) {
            persons[me to i] = 0
            persons[i to me] = 0
        }

        val maxHappiness = calculateMaxHappiness(personIds, persons)

        return maxHappiness.toString()
    }

    companion object {
        private val happinessRegex =
            Regex("""(\w+) would (gain|lose) (\d+) happiness units by sitting next to (\w+).""")

        private infix fun Int.to(other: Int) = IntPair(this, other)
    }
}
