package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResource
import java.util.ArrayDeque
import kotlin.math.pow

@AutoService(AOCSolution::class)
class Day17 : AOCSolution {
    override val year = 2024
    override val day = 17

    private data class Registers(var a: Long, var b: Long, var c: Long, var ip: Int = 0)
    private data class CPU(val registers: Registers, val instructions: List<Long>) {
        fun literalOperand(operand: Long): Long = operand
        fun comboOperand(operand: Long): Long = when (operand) {
            in 0..3 -> operand
            4L -> registers.a
            5L -> registers.b
            6L -> registers.c
            else -> error("Reserved operand")
        }

        fun runProgram(): List<Long> {
            val output = mutableListOf<Long>()
            while (registers.ip < instructions.size) {
                val instruction = instructions[registers.ip]
                val operand = instructions[registers.ip + 1]
                when (instruction) {
                    // ADV: A = A / 2^comboOperand
                    0L -> {
                        registers.a = (registers.a / 2.0.pow(comboOperand(operand).toDouble())).toLong()
                        registers.ip += 2
                    }
                    // BXL: B = B xor literalOperand
                    1L -> {
                        registers.b = registers.b xor literalOperand(operand)
                        registers.ip += 2
                    }
                    // BST: B = comboOperand % 8
                    2L -> {
                        registers.b = comboOperand(operand) % 8
                        registers.ip += 2
                    }
                    // JNZ: if A != 0, jump to literalOperand
                    3L -> {
                        if (registers.a != 0L) {
                            registers.ip = literalOperand(operand).toInt()
                        } else {
                            registers.ip += 2
                        }
                    }
                    // BXC: B = B xor C, operand is ignored
                    4L -> {
                        registers.b = registers.b xor registers.c
                        registers.ip += 2
                    }
                    // OUT: output comboOperand mod 8
                    5L -> {
                        output.add(comboOperand(operand) % 8L)
                        registers.ip += 2
                    }
                    // BDV: B = A / 2^comboOperand
                    6L -> {
                        registers.b = (registers.a / 2.0.pow(comboOperand(operand).toDouble())).toLong()
                        registers.ip += 2
                    }
                    // CDV: C = A / 2^comboOperand
                    7L -> {
                        registers.c = (registers.a / 2.0.pow(comboOperand(operand).toDouble())).toLong()
                        registers.ip += 2
                    }
                }
            }
            return output
        }
    }

    private fun parseCPU(input: String): CPU {
        val registerARegex = Regex("""Register A: (\d+)""")
        val registerBRegex = Regex("""Register B: (\d+)""")
        val registerCRegex = Regex("""Register C: (\d+)""")
        val programRegex = Regex("""Program: (.+)""")

        val (registerA) = registerARegex.find(input)?.destructured ?: error("Register A not found")
        val (registerB) = registerBRegex.find(input)?.destructured ?: error("Register B not found")
        val (registerC) = registerCRegex.find(input)?.destructured ?: error("Register C not found")
        val (program) = programRegex.find(input)?.destructured ?: error("Program not found")

        return CPU(
            Registers(registerA.toLong(), registerB.toLong(), registerC.toLong()),
            program.split(",").map { it.toLong() })
    }

    private data class SearchState(val offset: Int, val nextValue: Long)

    override fun part1(inputFile: String): String {
        val cpu = parseCPU(readResource(inputFile))

        return cpu.runProgram().joinToString(",")
    }

    override fun part2(inputFile: String): String {
        val cpu = parseCPU(readResource(inputFile))

        // Try to find the value that will make the program output the same as the instructions
        val stack = ArrayDeque<SearchState>()
        stack.add(SearchState(cpu.instructions.size - 1, 0))

        while (stack.isNotEmpty()) {
            val (offset, value) = stack.pollFirst()

            // Given that we are working with a 3-bit CPU, we try all possible values (0-7)
            for (i in 0 until 8) {
                val nextValue = (value shl 3) + i
                val output = cpu.run {
                    registers.a = nextValue
                    registers.b = 0
                    registers.c = 0
                    registers.ip = 0
                    runProgram()
                }

                if (output == cpu.instructions.drop(offset)) {
                    if (offset == 0) {
                        return nextValue.toString()
                    } else {
                        stack.add(SearchState(offset - 1, nextValue))
                    }
                }
            }
        }

        return "Not found"
    }
}