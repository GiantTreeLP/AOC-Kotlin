package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceLines
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.pow

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
                CONCATENATION -> {
                    // Fast concatenation assuming b is not 0 and both a and b are positive (https://math.stackexchange.com/a/578081)
                    a * 10.0.pow(ceil(log10((b + 1).toDouble()))).toLong() + b
                }
            }
        }
    }

    private data class Equation(
        val result: Long,
        val arguments: List<Long>,
        val allowedOperations: List<Operation>
    ) {
        fun nextOperation(index: Int, current: Long): Boolean {
            // If the current value is greater than the result, we can stop; it's not possible to reach the result
            if (current > result) {
                return false
            }
            // If we have reached the end of the arguments, return whether the current value is the result
            if (index == arguments.size) {
                return current == result
            }
            val next = arguments[index]
            return allowedOperations.any { op ->
                nextOperation(index + 1, op.apply(current, next))
            }
        }

        fun solvable(): Boolean {
            return nextOperation(1, arguments[0])
        }
    }

    private fun solveEquations(inputFile: String, allowedOperations: List<Operation>): String {
        val input = readResourceLines(inputFile)
        val equations = input.map { line ->
            val (resultString, argumentsString) = line.split(": ")
            val result = resultString.toLong()
            val arguments = argumentsString.split(' ').map { it.toLong() }
            Equation(result, arguments, allowedOperations)
        }

        val sumOfCorrectEquations = equations
            .filter { it.solvable() }
            .sumOf { it.result }
        return sumOfCorrectEquations.toString()
    }

    override fun part1(inputFile: String): String {
        return solveEquations(inputFile, listOf(Operation.ADDITION, Operation.MULTIPLICATION))
    }

    override fun part2(inputFile: String): String {
        return solveEquations(inputFile, listOf(Operation.ADDITION, Operation.MULTIPLICATION, Operation.CONCATENATION))
    }
}