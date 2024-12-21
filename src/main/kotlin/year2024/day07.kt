package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceLines

@AutoService(AOCSolution::class)
class Day07 : AOCSolution {
    override val year = 2024
    override val day = 7

    private enum class Operation {
        ADDITION, MULTIPLICATION, CONCATENATION;

        fun apply(a: Long, b: Long): Long {
            return when (this) {
                ADDITION -> a + b
                MULTIPLICATION -> a * b
                CONCATENATION -> "$a$b".toLong()
            }
        }
    }

    private data class Equation(
        val result: Long,
        val arguments: List<Long>,
        val allowedOperations: Set<Operation>
    ) {
        fun solutions(): List<Long> {
            fun nextOperation(index: Int, current: Long): List<Long> {
                // If we have reached the end of the arguments, return the current value
                if (index == arguments.size) {
                    return listOf(current)
                }
                if (current > result) {
                    return emptyList()
                }
                val next = arguments[index]
                return allowedOperations.flatMap { op ->
                    nextOperation(index + 1, op.apply(current, next))
                }
            }

            return nextOperation(1, arguments[0])
        }
    }

    private fun solveEquations(inputFile: String, allowedOperations: Set<Operation>): String {
        val input = readResourceLines(inputFile)
        val equations = input.map { line ->
            val (resultString, argumentsString) = line.split(Regex(": "))
            val result = resultString.trim().toLong()
            val arguments = argumentsString.split(' ').map { it.trim().toLong() }
            Equation(result, arguments, allowedOperations)
        }

        val sumOfCorrectEquations = equations
            .associateWith { it.solutions() }
            .filter { it.value.contains(it.key.result) }
            .map { it.key.result }
            .sum()
        return sumOfCorrectEquations.toString()
    }

    override fun part1(inputFile: String): String {
        return solveEquations(inputFile, setOf(Operation.ADDITION, Operation.MULTIPLICATION))
    }

    override fun part2(inputFile: String): String {
        return solveEquations(inputFile, setOf(Operation.ADDITION, Operation.MULTIPLICATION, Operation.CONCATENATION))
    }
}