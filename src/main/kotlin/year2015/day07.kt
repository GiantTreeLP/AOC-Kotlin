package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceLines

@AutoService(AOCSolution::class)
class Day07 : AOCSolution {
    override val year = 2015
    override val day = 7

    private sealed class Wire(val name: String) {
        abstract fun resolve(wires: MutableMap<String, Wire>): UShort

        class Immediate(name: String, val value: UShort) : Wire(name) {
            override fun resolve(wires: MutableMap<String, Wire>): UShort = value
        }

        class Copy(name: String, val copyWire: String) : Wire(name) {
            override fun resolve(wires: MutableMap<String, Wire>): UShort =
                wires.getValue(copyWire).resolve(wires).also {
                    // Store the resolved value in the map to avoid recalculating it
                    wires[name] = Immediate(name, it)
                }
        }

        class Not(name: String, val inputName: String) : Wire(name) {
            override fun resolve(wires: MutableMap<String, Wire>): UShort =
                wires.getValue(inputName).resolve(wires).inv().also {
                    // Store the resolved value in the map to avoid recalculating it
                    wires[name] = Immediate(name, it)
                }
        }

        class Operator(name: String, val input1Name: String, val input2Name: String, val op: String) : Wire(name) {
            override fun resolve(wires: MutableMap<String, Wire>): UShort {
                val input1 = wires.getValue(input1Name).resolve(wires)
                val input2 = wires.getValue(input2Name).resolve(wires)
                return when (op) {
                    "AND" -> input1 and input2
                    "OR" -> input1 or input2
                    "LSHIFT" -> ((input1.toInt() shl input2.toInt()) and 0xffff).toUShort()
                    "RSHIFT" -> ((input1.toInt() shr input2.toInt()) and 0xffff).toUShort()
                    else -> error("Unknown operator: $op")
                }.also {
                    // Store the resolved value in the map to avoid recalculating it
                    wires[name] = Immediate(name, it)
                }
            }
        }

    }

    private fun parseWires(
        input: List<String>,
    ): MutableMap<String, Wire> {
        return buildMap {

            input.forEach { line ->
                val immediateResult = immediate.matchEntire(line)
                if (immediateResult != null) {
                    val (value, wire) = immediateResult.destructured
                    val immediateValue = value.toUShortOrNull()
                    if (immediateValue != null) {
                        this[wire] = Wire.Immediate(wire, immediateValue)
                    } else {
                        this[wire] = Wire.Copy(wire, value)
                    }
                    return@forEach
                }

                val notResult = not.matchEntire(line)
                if (notResult != null) {
                    val (inputNot, outputWire) = notResult.destructured
                    this[outputWire] = Wire.Not(outputWire, inputNot)
                    return@forEach
                }

                val operatorResult = operator.matchEntire(line)
                if (operatorResult != null) {
                    val (input1, op, input2, outputWire) = operatorResult.destructured

                    // If the input is a number, store it as an immediate value
                    input1.toUShortOrNull()?.also {
                        this[input1] = Wire.Immediate(input1, it)
                    }
                    input2.toUShortOrNull()?.also {
                        this[input2] = Wire.Immediate(input2, it)
                    }
                    this[outputWire] = Wire.Operator(outputWire, input1, input2, op)
                    return@forEach
                }

                error("Unmatched line: $line")
            }
        }.toMutableMap()
    }

    override fun part1(inputFile: String): String {
        val input = readResourceLines(inputFile)

        val wires = parseWires(input)

        return if (inputFile.endsWith("sample")) {
            wires.getValue("d").resolve(wires).toString()
        } else {
            wires.getValue("a").resolve(wires).toString()
        }
    }

    override fun part2(inputFile: String): String {
        if (inputFile.endsWith("sample")) {
            return "There is no sample for part 2"
        }

        val input = readResourceLines(inputFile)

        val wires = parseWires(input)

        // Copy the wires to avoid modifying the original map
        val wiresCopy = wires.toMutableMap()

        // Get the value of wire "a" in the first run and override the value of wire "b" with it
        val a = wires.getValue("a").resolve(wires)
        wiresCopy["b"] = Wire.Immediate("b", a)

        // Calculate the new value of wire "a"
        return wiresCopy.getValue("a").resolve(wiresCopy).toString()
    }

    companion object Regexes {
        private val immediate = Regex("""(\w+) -> (\w+)""")
        private val not = Regex("""NOT (\w+) -> (\w+)""")
        private val operator = Regex("""(\w+) (AND|OR|LSHIFT|RSHIFT) (\w+) -> (\w+)""")
    }
}
