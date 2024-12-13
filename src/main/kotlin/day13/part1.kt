package day13

import common.Point
import common.readResource
import day13.Part1.findFactorsToSum

private object Part1 {
    const val BUTTON_A_PRIZE = 3
    const val BUTTON_B_PRIZE = 1

    val buttonARegex = Regex("""Button A: X\+(\d+), Y\+(\d+)""")
    val buttonBRegex = Regex("""Button B: X\+(\d+), Y\+(\d+)""")
    val prizeRegex = Regex("""Prize: X=(\d+), Y=(\d+)""")

    data class Button(val move: Point)

    data class ClawMachine(val buttonA: Button, val buttonB: Button, val prize: Point)

    fun findFactorsToSum(target: Long, a: Long, b: Long): Set<Pair<Long, Long>> {
        return buildSet {
            // X = N * a + M * b
            for (n in 0..target / a) {
                val m = (target - n * a) / b
                if (n * a + m * b == target) {
                    add(n to m)
                }
            }
        }
    }
}

fun main() {
    // Parse the claw machines
    val clawMachines = readResource("day13/input").split("""\n\n""".toRegex()).map { block ->
        val lines = block.lines()
        val buttonA = Part1.buttonARegex.find(lines[0])!!.destructured
            .let { (x, y) -> Part1.Button(Point(x.toLong(), y.toLong())) }
        val buttonB = Part1.buttonBRegex.find(lines[1])!!.destructured
            .let { (x, y) -> Part1.Button(Point(x.toLong(), y.toLong())) }
        val prize = Part1.prizeRegex.find(lines[2])!!.destructured.let { (x, y) -> Point(x.toLong(), y.toLong()) }
        Part1.ClawMachine(buttonA, buttonB, prize)
    }

    // Find the cheapest solution for each claw machine
    // X = N * buttonAX + M * buttonBX
    // Y = N * buttonAY + M * buttonBY
    // Prize = N * prizeX + M * prizeY
    // Goal is to minimize Prize

    val cheapestTokens = clawMachines.map {
        val factorsX = findFactorsToSum(it.prize.x, it.buttonA.move.x, it.buttonB.move.x)
        val factorsY = findFactorsToSum(it.prize.y, it.buttonA.move.y, it.buttonB.move.y)

        val solutions = factorsX.intersect(factorsY)

        solutions.minOfOrNull { (n, m) -> n * Part1.BUTTON_A_PRIZE + m * Part1.BUTTON_B_PRIZE } ?: 0
    }


    println(cheapestTokens.sum())
}
