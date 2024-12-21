package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.Point
import common.readResource

@AutoService(AOCSolution::class)
class Day13 : AOCSolution {
    override val year = 2024
    override val day = 13

    private val buttonARegex = Regex("""Button A: X\+(\d+), Y\+(\d+)""")
    private val buttonBRegex = Regex("""Button B: X\+(\d+), Y\+(\d+)""")
    private val prizeRegex = Regex("""Prize: X=(\d+), Y=(\d+)""")
    private val part2Offset = Point(10_000_000_000_000, 10_000_000_000_000)

    private data class ClawMachine(val buttonA: Point, val buttonB: Point, val prize: Point)

    private fun findFactorsToSum(target: Long, a: Long, b: Long): Set<Pair<Long, Long>> {
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

    private fun parseClawMachines(inputFile: String): List<ClawMachine> {
        val clawMachines = readResource(inputFile).split("""\n\n""".toRegex()).map { block ->
            val lines = block.lines()
            val buttonA = buttonARegex.find(lines[0])!!.destructured
                .let { (x, y) -> Point(x.toLong(), y.toLong()) }
            val buttonB = buttonBRegex.find(lines[1])!!.destructured
                .let { (x, y) -> Point(x.toLong(), y.toLong()) }
            val prize = prizeRegex.find(lines[2])!!.destructured
                .let { (x, y) -> Point(x.toLong(), y.toLong()) }
            ClawMachine(buttonA, buttonB, prize)
        }
        return clawMachines
    }

    override fun part1(inputFile: String): String {
        val clawMachines = parseClawMachines(inputFile)

        // Find the cheapest solution for each claw machine
        // X = N * buttonAX + M * buttonBX
        // Y = N * buttonAY + M * buttonBY
        // Prize = N * prizeX + M * prizeY
        // Goal is to minimize Prize

        val cheapestTokens = clawMachines.map {
            val factorsX = findFactorsToSum(it.prize.x, it.buttonA.x, it.buttonB.x)
            val factorsY = findFactorsToSum(it.prize.y, it.buttonA.y, it.buttonB.y)

            val solutions = factorsX.intersect(factorsY)

            solutions.minOfOrNull { (n, m) -> n * BUTTON_A_PRIZE + m * BUTTON_B_PRIZE } ?: 0
        }


        return cheapestTokens.sum().toString()
    }

    override fun part2(inputFile: String): String {
        val clawMachines = parseClawMachines(inputFile)
            .map { (buttonA, buttonB, prize) ->
                ClawMachine(
                    buttonA,
                    buttonB,
                    prize + part2Offset
                )
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
                (it.buttonB.y * it.prize.x - it.buttonB.x * it.prize.y) / (it.buttonA.x * it.buttonB.y - it.buttonB.x * it.buttonA.y)
            val m =
                (it.buttonA.y * it.prize.x - it.buttonA.x * it.prize.y) / (it.buttonB.x * it.buttonA.y - it.buttonA.x * it.buttonB.y)

            val reachedX = n * it.buttonA.x + m * it.buttonB.x
            val reachedY = n * it.buttonA.y + m * it.buttonB.y

            if (reachedX == it.prize.x && reachedY == it.prize.y) {
                n * BUTTON_A_PRIZE + m * BUTTON_B_PRIZE
            } else {
                0
            }
        }

        return cheapestTokens.sum().toString()
    }

    companion object {
        private const val BUTTON_A_PRIZE = 3
        private const val BUTTON_B_PRIZE = 1
    }
}