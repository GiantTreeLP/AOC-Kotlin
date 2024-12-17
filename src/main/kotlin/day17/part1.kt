package day17

import common.readResource
import kotlin.math.pow


private object Part1 {
    data class Registers(var a: Int, var b: Int, var c: Int, var ip: Int = 0)
    data class CPU(val registers: Registers, val instructions: List<Int>) {
        fun literalOperand(operand: Int): Int = operand
        fun comboOperand(operand: Int): Int = when (operand) {
            in 0..3 -> operand
            4 -> registers.a
            5 -> registers.b
            6 -> registers.c
            else -> error("Reserved operand")
        }

        fun runProgram(): List<Int> {
            val output = mutableListOf<Int>()
            while (registers.ip < instructions.size) {
                val instruction = instructions[registers.ip]
                val operand = instructions[registers.ip + 1]
                when (instruction) {
                    // ADV: A = A / 2^comboOperand
                    0 -> {
                        registers.a = (registers.a / 2.0.pow(comboOperand(operand))).toInt()
                        registers.ip += 2
                    }
                    // BXL: B = B xor literalOperand
                    1 -> {
                        registers.b = registers.b xor literalOperand(operand)
                        registers.ip += 2
                    }
                    // BST: B = comboOperand % 8
                    2 -> {
                        registers.b = comboOperand(operand) % 8
                        registers.ip += 2
                    }
                    // JNZ: if A != 0, jump to literalOperand
                    3 -> {
                        if (registers.a != 0) {
                            registers.ip = literalOperand(operand)
                        } else {
                            registers.ip += 2
                        }
                    }
                    // BXC: B = B xor C, operand is ignored
                    4 -> {
                        registers.b = registers.b xor registers.c
                        registers.ip += 2
                    }
                    // OUT: output comboOperand mod 8
                    5 -> {
                        output.add(comboOperand(operand) % 8)
                        registers.ip += 2
                    }
                    // BDV: B = A / 2^comboOperand
                    6 -> {
                        registers.b = (registers.a / 2.0.pow(comboOperand(operand))).toInt()
                        registers.ip += 2
                    }
                    // CDV: C = A / 2^comboOperand
                    7 -> {
                        registers.c = (registers.a / 2.0.pow(comboOperand(operand))).toInt()
                        registers.ip += 2
                    }
                }
            }
            return output
        }
    }

    fun parseCPU(input: String): CPU {
        val registerARegex = Regex("""Register A: (\d+)""")
        val registerBRegex = Regex("""Register B: (\d+)""")
        val registerCRegex = Regex("""Register C: (\d+)""")
        val programRegex = Regex("""Program: (.+)""")

        val (registerA) = registerARegex.find(input)?.destructured ?: error("Register A not found")
        val (registerB) = registerBRegex.find(input)?.destructured ?: error("Register B not found")
        val (registerC) = registerCRegex.find(input)?.destructured ?: error("Register C not found")
        val (program) = programRegex.find(input)?.destructured ?: error("Program not found")

        return CPU(
            Registers(registerA.toInt(), registerB.toInt(), registerC.toInt()),
            program.split(",").map { it.toInt() })
    }
}

fun main() {
    val input = readResource("day17/input")
    val cpu = Part1.parseCPU(input)

    println(cpu)

    val output = cpu.runProgram().joinToString(",")
    println(output)
}