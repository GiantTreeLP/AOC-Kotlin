package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceTwoParts

@AutoService(AOCSolution::class)
class Day24 : AOCSolution {
    override val year = 2024
    override val day = 24

    enum class Operation {
        AND, OR, XOR
    }

    sealed class Gate(open val name: String) {
        data class Static(override val name: String, val value: Long) : Gate(name)
        data class GateOutput(
            override val name: String,
            val operation: Operation,
            val wire1: String,
            val wire2: String
        ) : Gate(name)

        /**
         * Recursively resolve the value of the gate
         */
        fun resolve(wires: Map<String, Gate>): Long {
            return when (this) {
                is Static -> value
                is GateOutput -> {
                    val wire1Value = wires.getValue(wire1).resolve(wires)
                    val wire2Value = wires.getValue(wire2).resolve(wires)

                    when (operation) {
                        Operation.AND -> wire1Value and wire2Value
                        Operation.OR -> wire1Value or wire2Value
                        Operation.XOR -> wire1Value xor wire2Value
                    }
                }
            }
        }
    }

    private fun parseInput(inputFile: String): MutableMap<String, Gate> {
        val (wires, gates) = readResourceTwoParts(inputFile)

        val wireMap = mutableMapOf<String, Gate>()
        wires.lines().forEach { line ->
            val (name, value) = staticWireRegex.matchEntire(line)!!.destructured
            wireMap[name] = Gate.Static(name, value.toLong())
        }
        gates.lines().forEach { line ->
            val (wire1, operation, wire2, name) = gateRegex.matchEntire(line)!!.destructured
            wireMap[name] = Gate.GateOutput(name, Operation.valueOf(operation), wire1, wire2)
        }

        return wireMap
    }

    private fun graphvizWireMap(
        prunedMap: MutableMap<String, Gate>,
        wireMap: MutableMap<String, Gate>
    ) {
        println("digraph Adder {")
//        println("\tsubgraph Inputs {")
//        prunedMap
//            .filter { it.value.name.startsWith('x') }
//            .toList()
//            .sortedBy { it.second.name.substring(1..2).toInt() }
//            .forEach { (key, value) ->
//                println("\t\t${value.name}")
//            }
//        println("\t}")

        println("\tsubgraph Outputs {")
        prunedMap
            .filter { it.value.name.startsWith('z') }
            .toList()
            .sortedBy { it.second.name.substring(1..2).toInt() }
            .forEach { (key, value) ->
                if (value is Gate.GateOutput) {
                    println("\t\t${value.name} [label=\"${value.operation} ($key)\"]")
                }
            }
        println("\t}")

        prunedMap
            .filter { it.key[0] !in setOf('x', 'y') }
            .forEach { (key, value) ->
                when (value) {
                    is Gate.Static -> println(key)
                    is Gate.GateOutput -> {
                        val operation1 =
                            (wireMap.getValue(value.wire1) as? Gate.GateOutput)?.operation?.toString() ?: "Static"
                        val operation2 =
                            (wireMap.getValue(value.wire2) as? Gate.GateOutput)?.operation?.toString() ?: "Static"
                        println("${value.wire1} -> $key [label=${operation1}]")
                        println("${value.wire2} -> $key [label=${operation2}]")
                        println("""$key [label="${value.operation} ($key)"]""")
                    }
                }
            }

        println("}")
    }

    private fun pruneMap(wireMap: MutableMap<String, Gate>): MutableMap<String, Gate> {
        // Prune the graph of probably correct connections
        val prunedMap = wireMap.toMutableMap()
        wireMap.forEach { (key, value) ->
            if (value is Gate.GateOutput) {
                // Remove all z gates that are XOR gates, those are correct
                if (value.name.startsWith('z') &&
                    value.operation == Operation.XOR
                ) {
                    prunedMap.remove(key)
                }
                val input1 = wireMap.getValue(value.wire1)
                val input2 = wireMap.getValue(value.wire2)
                if (!value.name.startsWith('z')) {
                    if (value.operation == Operation.OR) {
                        if (input1 is Gate.GateOutput &&
                            input1.operation == Operation.AND &&
                            input2 is Gate.GateOutput &&
                            input2.operation == Operation.AND
                        ) {
                            // Remove all OR gates that are correctly connected to two AND gates
                            // These are correct carry bits
                            prunedMap.remove(key)
                        }
                    } else if (value.operation == Operation.AND) {
                        if (input1 is Gate.GateOutput &&
                            input1.operation == Operation.XOR &&
                            input2 is Gate.GateOutput &&
                            input2.operation == Operation.OR
                        ) {
                            // Remove all AND gates that are correctly connected to an XOR and an OR gate
                            // These are correctly connected to the current half adder and
                            // the carry bit calculation of the previous bit
                            prunedMap.remove(key)
                        } else if (input1 is Gate.GateOutput &&
                            input1.operation == Operation.OR &&
                            input2 is Gate.GateOutput &&
                            input2.operation == Operation.XOR
                        ) {
                            // Remove all AND gates that are correctly connected to an XOR and an OR gate
                            // These are correctly connected to the current half adder and
                            // the carry bit calculation of the previous bit
                            prunedMap.remove(key)
                        }
                    }
                    // Remove all XOR gates that are correctly connected to two static values (A xor B)
                    // These are correct half adders
                    // Also remove all AND gates that are correctly connected to two static values (A and B)
                    // These are correct halves of the carry bit calculation
                    if (input1 is Gate.Static &&
                        input2 is Gate.Static &&
                        value.operation in setOf(Operation.AND, Operation.XOR)
                    ) {
                        prunedMap.remove(key)
                    }
                }
            }
        }
        prunedMap.remove("z45") // Is correctly connected to the previous bit (Carry bit calculation)
        return prunedMap
    }

    override fun part1(inputFile: String): String {
        val wireMap = parseInput(inputFile)

        // Find all z bits and resolve them
        val bits = wireMap
            .filter { it.value.name.startsWith('z') }
            .mapValues { it.value.resolve(wireMap) }
            .toSortedMap()
            .values
            .toList()

        val result = bits.foldIndexed(0L) { index, acc, bit -> acc or (bit shl index) }

        return result.toString()
    }

    override fun part2(inputFile: String): String {
        if (inputFile.endsWith("sample")) {
            return "Not implemented for sample input"
        }

        val wrongResult = part1(inputFile).toLong()
        val wireMap = parseInput(inputFile)
        val prunedMap = pruneMap(wireMap)

        graphvizWireMap(prunedMap, wireMap)

        // Find all x bits and resolve them
        val number1 = wireMap
            .filter { it.key.startsWith('x') }
            .mapValues { it.value.resolve(wireMap) }
            .toSortedMap()
            .values
            .toList()
            .foldIndexed(0L) { index, acc, bit -> acc or (bit shl index) }

        // Find all y bits and resolve them
        val number2 = wireMap
            .filter { it.key.startsWith('y') }
            .mapValues { it.value.resolve(wireMap) }
            .toSortedMap()
            .values
            .toList()
            .foldIndexed(0L) { index, acc, bit -> acc or (bit shl index) }

        val correctSum = number1 + number2

        val differences = correctSum xor wrongResult

        println("Number of differences: ${differences.countOneBits()}")

        // Do the swaps
        //  jgb <-> z20
        //  vcg <-> z24
        //  rvc <-> rrs
        //  z09 <-> rkf
        val jgb = wireMap.getValue("jgb") as Gate.GateOutput
        val z20 = wireMap.getValue("z20") as Gate.GateOutput

        wireMap["jgb"] = Gate.GateOutput("jgb", z20.operation, z20.wire1, z20.wire2)
        wireMap["z20"] = Gate.GateOutput("z20", jgb.operation, jgb.wire1, jgb.wire2)

        val vcg = wireMap.getValue("vcg") as Gate.GateOutput
        val z24 = wireMap.getValue("z24") as Gate.GateOutput

        wireMap["vcg"] = Gate.GateOutput("vcg", z24.operation, z24.wire1, z24.wire2)
        wireMap["z24"] = Gate.GateOutput("z24", vcg.operation, vcg.wire1, vcg.wire2)

        val rvc = wireMap.getValue("rvc") as Gate.GateOutput
        val rrs = wireMap.getValue("rrs") as Gate.GateOutput

        wireMap["rvc"] = Gate.GateOutput("rvc", rrs.operation, rrs.wire1, rrs.wire2)
        wireMap["rrs"] = Gate.GateOutput("rrs", rvc.operation, rvc.wire1, rvc.wire2)

        val z09 = wireMap.getValue("z09") as Gate.GateOutput
        val rkf = wireMap.getValue("rkf") as Gate.GateOutput

        wireMap["z09"] = Gate.GateOutput("z09", rkf.operation, rkf.wire1, rkf.wire2)
        wireMap["rkf"] = Gate.GateOutput("rkf", z09.operation, z09.wire1, z09.wire2)

//        val prunedMap = pruneMap(wireMap)
//        graphvizWireMap(prunedMap, wireMap)

        // Find all z bits and resolve them
        val bits = wireMap
            .filter { it.value.name.startsWith('z') }
            .mapValues { it.value.resolve(wireMap) }
            .toSortedMap()
            .values
            .toList()

        val result = bits.foldIndexed(0L) { index, acc, bit -> acc or (bit shl index) }

        val newDifferences = correctSum xor result
        println("Number of differences after swaps: ${newDifferences.countOneBits()}")

        return listOf("jgb", "z20", "vcg", "z24", "rvc", "rrs", "z09", "rkf").sorted().joinToString(",")
    }

    companion object {
        val staticWireRegex = Regex("""(.*): (\d)""")

        val gateRegex = Regex("""(.*) (AND|OR|XOR) (.*) -> (.*)""")
    }
}