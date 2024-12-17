package day7

import common.readResourceLines

private object Part2 {
    enum class Operation {
        ADDITION, MULTIPLICATION, CONCATENATION;

        fun apply(a: Long, b: Long): Long {
            return when (this) {
                ADDITION -> a + b
                MULTIPLICATION -> a * b
                CONCATENATION -> "$a$b".toLong()
            }
        }
    }

    data class Equation(val result: Long, val arguments: List<Long>) {
        fun solutions(): List<Long> {
            fun nextOperation(index: Int, current: Long): List<Long> {
                // If we have reached the end of the arguments, return the current value
                if (index == arguments.size) {
                    return listOf(current)
                }
                val next = arguments[index]
                return Operation.entries.flatMap { op ->
                    nextOperation(index + 1, op.apply(current, next))
                }
            }

            return nextOperation(1, arguments[0])
        }
    }
}

fun main() {
    val input = readResourceLines("day7/input")
    val equations = input.map { line ->
        val (resultString, argumentsString) = line.split(Regex(": "))
        val result = resultString.trim().toLong()
        val arguments = argumentsString.split(' ').map { it.trim().toLong() }
        Part2.Equation(result, arguments)
    }

    val sumOfCorrectEquations = equations
        .associateWith { it.solutions() }
        .filter { it.value.contains(it.key.result) }
        .map { it.key.result }
        .sum()
    println(sumOfCorrectEquations)
}
