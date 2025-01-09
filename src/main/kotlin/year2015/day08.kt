package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceLines

@AutoService(AOCSolution::class)
class Day08 : AOCSolution {
    override val year = 2015
    override val day = 8

    override fun part1(inputFile: String): String {
        val inputLines = readResourceLines(inputFile)

        val codeChars = inputLines.sumOf { it.length }

        val memoryChars = inputLines.sumOf { line ->
            var count = 0

            // Skip the surrounding quotes
            var i = 1
            while (i < line.length - 1) {
                if (line[i] == '\\') {
                    when {
                        // Skip the next character, as it's escaped
                        line[i + 1] == '\\' || line[i + 1] == '"' -> {
                            i += 2
                        }

                        // Skip the next 3 characters, as it's an escaped hex code
                        line[i + 1] == 'x' -> {
                            i += 4
                        }
                    }
                } else {
                    i++
                }
                count++
            }

            count

        }

        return (codeChars - memoryChars).toString()
    }

    override fun part2(inputFile: String): String {
        val inputLines = readResourceLines(inputFile)

        val codeChars = inputLines.sumOf { it.length }

        val encodedChars = inputLines.sumOf { line ->
            // 2 for the surrounding quotes
            2 + line.sumOf {
                when (it) {
                    // 2 for the escape character and the character itself
                    '\\', '"' -> {
                        2L
                    }

                    else -> {
                        1
                    }
                }
            }
        }

        return (encodedChars - codeChars).toString()
    }
}
