package day13

import common.Point
import common.readResource

private object Part2 {
    const val BUTTON_A_PRIZE = 3
    const val BUTTON_B_PRIZE = 1

    val buttonARegex = Regex("""Button A: X\+(\d+), Y\+(\d+)""")
    val buttonBRegex = Regex("""Button B: X\+(\d+), Y\+(\d+)""")
    val prizeRegex = Regex("""Prize: X=(\d+), Y=(\d+)""")

    val part2Offset = Point(10_000_000_000_000, 10_000_000_000_000)

    data class Button(val move: Point)

    data class ClawMachine(val buttonA: Button, val buttonB: Button, val prize: Point)
}

fun main() {
    // Parse the claw machines
    val clawMachines = readResource("day13/input").split("""\n\n""".toRegex()).map { block ->
        val lines = block.lines()

        val buttonA = Part2.buttonARegex.find(lines[0])!!.destructured
            .let { (x, y) -> Part2.Button(Point(x.toLong(), y.toLong())) }

        val buttonB = Part2.buttonBRegex.find(lines[1])!!.destructured
            .let { (x, y) -> Part2.Button(Point(x.toLong(), y.toLong())) }

        val prize = Part2.part2Offset + Part2.prizeRegex.find(lines[2])!!.destructured
            .let { (x, y) -> Point(x.toLong(), y.toLong()) }

        Part2.ClawMachine(buttonA, buttonB, prize)
    }

    // Find the cheapest solution for each claw machine
    // X = N * buttonAX + M * buttonBX
    // Y = N * buttonAY + M * buttonBY
    // Prize = N * prizeX + M * prizeY (N * 3 + M * 1)

    // N = (Y - M * buttonBY) / buttonAY
    // X = (buttonAX * (Y - M * buttonBY) / buttonAY) + (M * buttonBX)
    // X * buttonAY = (buttonAX * Y) - (buttonAX * buttonBY * M) + (buttonAY * buttonBX * M)
    // X * buttonAY - Y * buttonAX = M * (buttonAY * buttonBX - buttonAX * buttonBY)

    // M = (X * buttonAY - Y * buttonAX) / (buttonAY * buttonBX - buttonAX * buttonBY)

    // M = (Y - N * buttonAY) / buttonBY
    // X = (N * buttonAX) + ((Y - N * buttonAY) / buttonBY) * buttonBX
    // X * buttonBY = (N * buttonAX * buttonBY) + (Y * buttonBX) - (N * buttonAY * buttonBX)
    // X * buttonBY - Y * buttonBX = N * (buttonAX * buttonBY - buttonAY * buttonBX)

    // N = (X * buttonBY - Y * buttonBX) / (buttonAX * buttonBY - buttonBX * buttonAY)
    val cheapestTokens = clawMachines.map {
        val n =
            (it.buttonB.move.y * it.prize.x - it.buttonB.move.x * it.prize.y) / (it.buttonA.move.x * it.buttonB.move.y - it.buttonB.move.x * it.buttonA.move.y)
        val m =
            (it.buttonA.move.y * it.prize.x - it.buttonA.move.x * it.prize.y) / (it.buttonB.move.x * it.buttonA.move.y - it.buttonA.move.x * it.buttonB.move.y)

        val reachedX = n * it.buttonA.move.x + m * it.buttonB.move.x
        val reachedY = n * it.buttonA.move.y + m * it.buttonB.move.y

        if (reachedX == it.prize.x && reachedY == it.prize.y) {
            n * Part2.BUTTON_A_PRIZE + m * Part2.BUTTON_B_PRIZE
        } else {
            0
        }
    }

//    println(cheapestTokens)
    println(cheapestTokens.sum())
}
