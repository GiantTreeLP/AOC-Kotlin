package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.Point
import common.readResourceLines
import kotlin.math.abs
import kotlin.math.sign

@AutoService(AOCSolution::class)
class Day21 : AOCSolution {
    override val year = 2024
    override val day = 21

    private val digitsRegex = Regex("""\d+""")

    /**
     * +---+---+---+
     * | 7 | 8 | 9 |
     * +---+---+---+
     * | 4 | 5 | 6 |
     * +---+---+---+
     * | 1 | 2 | 3 |
     * +---+---+---+
     *     | 0 | A |
     *     +---+---+
     *
     * A 3 x 4 grid with an empty space in the bottom left corner.
     **/
    private val keypad = buildMap {
        listOf(
            "789",
            "456",
            "123",
            " 0A"
        ).forEachIndexed { y, row ->
            row.forEachIndexed { x, c ->
                put(c, Point(x, y))
            }
        }
    }

    /**
     *     +---+---+
     *     | ^ | A |
     * +---+---+---+
     * | < | v | > |
     * +---+---+---+
     *
     * A 3 x 2 grid with an empty space in the top left corner.
     */
    private val directionalPad = buildMap {
        listOf(
            " ^A",
            "<v>",
        ).forEachIndexed { y, row ->
            row.forEachIndexed { x, c ->
                put(c, Point(x, y))
            }
        }
    }

    private val lengthCache = mutableMapOf<Pair<String, Int>, Long>()

    private fun getMoves(from: Char, to: Char, numpad: Boolean): String {
        val pad = if (numpad) {
            keypad
        } else {
            directionalPad
        }

        // Get the connecting vector between the two points
        val fromPoint = pad.getValue(from)
        val toPoint = pad.getValue(to)
        val move = toPoint - fromPoint

        // Get the required horizontal and vertical moves
        val horizontal = when (move.x.sign) {
            -1 -> {
                LEFT.toString().repeat(abs(move.x.toInt()))
            }

            1 -> {
                RIGHT.toString().repeat(abs(move.x.toInt()))
            }

            else -> {
                ""
            }
        }

        val vertical = when (move.y.sign) {
            -1 -> {
                UP.toString().repeat(abs(move.y.toInt()))
            }

            1 -> {
                DOWN.toString().repeat(abs(move.y.toInt()))
            }

            else -> {
                ""
            }
        }

        // Make sure we are not crossing the space character
        // Prefer not moving left
        val moveStrings = buildList {
            if (pad[SPACE] != Point(fromPoint.x, toPoint.y)) {
                add("$vertical${horizontal}A")
            }
            if (pad[SPACE] != Point(toPoint.x, fromPoint.y)) {
                add("$horizontal${vertical}A")
            }
        }

        return if (moveStrings.size < 2 || move.x > 0) {
            moveStrings.first()
        } else {
            moveStrings.last()
        }
    }

    private fun subCodeLength(from: Char, to: Char, depth: Int, numpad: Boolean): Long {
        return codeLength(getMoves(from, to, numpad), depth - 1, false)
    }

    private fun codeLength(input: String, depth: Int, numpad: Boolean): Long {
        return lengthCache.getOrPut(input to depth) {
            if (depth == 0) {
                input.length.toLong()
            } else {
                (ACTIVATE + input).toList().zipWithNext().sumOf { (from, to) ->
                    subCodeLength(from, to, depth, numpad)
                }
            }
        }
    }


    override fun part1(inputFile: String): String {
        val input = readResourceLines(inputFile)

        val complexities = input.sumOf { keypadInput ->
            val myInputLength = codeLength(keypadInput, 3, true)

            digitsRegex.find(keypadInput)!!.value.toInt() * myInputLength
        }

        return complexities.toString()
    }

    override fun part2(inputFile: String): String {
        val input = readResourceLines(inputFile)

        val complexities = input.sumOf { keypadInput ->
            val myInputLength = codeLength(keypadInput, 26, true)

            digitsRegex.find(keypadInput)!!.value.toInt() * myInputLength
        }

        return complexities.toString()
    }

    companion object {
        private const val LEFT = '<'
        private const val RIGHT = '>'
        private const val UP = '^'
        private const val DOWN = 'v'
        private const val ACTIVATE = 'A'
        private const val SPACE = ' '
    }
}