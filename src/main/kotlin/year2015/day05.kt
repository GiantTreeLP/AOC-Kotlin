package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceLines

@AutoService(AOCSolution::class)
class Day05 : AOCSolution {
    override val year = 2015
    override val day = 5

    private fun isNiceString(string: String): Boolean {
        val vowels = string.count { it in VOWELS }
        if (vowels < 3) {
            return false
        }

        val doubleLetter = string.zipWithNext().any { it.first == it.second }
        if (!doubleLetter) {
            return false
        }

        if (forbidden.any { it in string }) {
            return false
        }

        return true
    }

    private fun isNiceStringPart2(string: String): Boolean {
        val windows = string.windowedSequence(2)

        val pairsTwice = windows.withIndex().any { (index, window) ->
            string.indexOf(window, index + 2) != -1
        }

        if (!pairsTwice) {
            return false
        }

        for (i in 0 until string.length - 2) {
            if (string[i] == string[i + 2]) {
                return true
            }
        }
        return false
    }


    override fun part1(inputFile: String): String {
        val strings = readResourceLines(inputFile)

        return strings.count { isNiceString(it) }.toString()
    }

    override fun part2(inputFile: String): String {
        val strings = readResourceLines(inputFile)

        return strings.count { isNiceStringPart2(it) }.toString()
    }

    companion object {
        private const val VOWELS = "aeiou"
        private val forbidden = arrayOf("ab", "cd", "pq", "xy")
    }
}