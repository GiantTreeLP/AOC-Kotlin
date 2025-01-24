package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceLines
import kotlin.math.min

@AutoService(AOCSolution::class)
class Day14 : AOCSolution {
    override val year = 2015
    override val day = 14

    private data class Reindeer(val name: String, val speed: Int, val flyTime: Int, val restTime: Int)

    private fun fillDistances(
        reindeer: List<Reindeer>,
        raceDuration: Int,
        distances: IntArray
    ) {
        for (i in reindeer.indices) {
            val r = reindeer[i]

            val cycleTime = r.flyTime + r.restTime
            val fullCycles = raceDuration / cycleTime

            // Remaining time in the last partial cycle
            val remainingTime = raceDuration % cycleTime
            val remainingFlyTime = min(r.flyTime, remainingTime)

            distances[i] = fullCycles * r.speed * r.flyTime + remainingFlyTime * r.speed
        }
    }

    private fun runRacePart1(reindeer: List<Reindeer>, raceDuration: Int): Int {
        val distances = IntArray(reindeer.size)

        fillDistances(reindeer, raceDuration, distances)

        return distances.max()
    }

    private fun runRacePart2(reindeer: List<Reindeer>, raceDuration: Int): Int {
        val distances = IntArray(reindeer.size)
        val points = IntArray(reindeer.size)

        for (t in 1..raceDuration) {
            fillDistances(reindeer, t, distances)

            // Award points to the leading reindeer(s)
            val maxDistance = distances.max()
            for (i in reindeer.indices) {
                if (distances[i] == maxDistance) {
                    points[i]++
                }
            }
        }

        return points.max()
    }

    private fun parseReindeer(inputFile: String): List<Reindeer> {
        val lines = readResourceLines(inputFile)
        val reindeer = lines.map { line ->
            val match = reindeerRegex.matchEntire(line) ?: error("Invalid line: $line")
            val name = match.groupValues[1]
            val speed = match.groupValues[2].toInt()
            val flyTime = match.groupValues[3].toInt()
            val restTime = match.groupValues[4].toInt()
            Reindeer(name, speed, flyTime, restTime)
        }
        return reindeer
    }

    override fun part1(inputFile: String): String {
        val raceDuration = if (inputFile.endsWith("sample")) {
            SAMPLE_RACE_DURATION
        } else {
            RACE_DURATION
        }

        val reindeer = parseReindeer(inputFile)

        val winningDistance = runRacePart1(reindeer, raceDuration)

        return winningDistance.toString()
    }

    override fun part2(inputFile: String): String {
        val raceDuration = if (inputFile.endsWith("sample")) {
            SAMPLE_RACE_DURATION
        } else {
            RACE_DURATION
        }

        val reindeer = parseReindeer(inputFile)

        val winningScore = runRacePart2(reindeer, raceDuration)

        return winningScore.toString()
    }

    companion object {
        const val SAMPLE_RACE_DURATION = 1000
        const val RACE_DURATION = 2503
        private val reindeerRegex =
            Regex("""(\w+) can fly (\d+) km/s for (\d+) seconds, but then must rest for (\d+) seconds\.""")
    }
}
