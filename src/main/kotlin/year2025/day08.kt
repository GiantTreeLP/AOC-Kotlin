package year2025

import com.google.auto.service.AutoService
import common.AOCSolution
import common.grid.replaceAll
import common.readResourceLines
import java.util.*

@AutoService(AOCSolution::class)
class Day08 : AOCSolution {
    override val year = 2025
    override val day = 8

    override fun part1(inputFile: String): String {
        val numConnections = numberOfConnections(inputFile)
        val junctionBoxes = readResourceLines(inputFile).map { line ->
            val (x, y, z) = line.split(",").map { it.toInt() }
            JunctionBox(x, y, z)
        }

        val connections = buildShortestConnections(junctionBoxes, numConnections)
        // The connections are ordered from longest to shortest, but because
        // all connections need to be used, the order doesn't matter
        val circuitSizes = buildCircuitSizes(junctionBoxes, connections)

        // Calculate the result as the product of the sizes
        // of the three largest circuits
        var result = circuitSizes[0]
        for (i in 1 until 3) {
            result *= circuitSizes[i]
        }
        return result.toString()
    }

    override fun part2(inputFile: String): String {
        val junctionBoxes = readResourceLines(inputFile).map { line ->
            val (x, y, z) = line.split(",").map { it.toInt() }
            JunctionBox(x, y, z)
        }

        val connections = buildHeuristicConnections(junctionBoxes, 12)

        val (box1Index, box2Index) = buildCompleteCircuit(junctionBoxes, connections)
        val (x1) = junctionBoxes[box1Index]
        val (x2) = junctionBoxes[box2Index]

        return (x1.toLong() * x2.toLong()).toString()
    }

    /**
     * Build connections between [JunctionBox]es keeping the [connectionLimit] shortest.
     *
     * @param junctionBoxes the junction boxes to connect
     * @param connectionLimit the maximum amount of connections to keep
     * @return a queue/heap of connections between junction boxes
     */
    private fun buildShortestConnections(
        junctionBoxes: List<JunctionBox>,
        connectionLimit: Int
    ): Queue<Connection> {
        // Connections between junction boxes as a max heap.
        val shortestEdges = PriorityQueue<Connection>(connectionLimit)

        junctionBoxes.forEachIndexed { index, box ->
            // +1 to skip connections from the junction box to itself
            for (j in index + 1 until junctionBoxes.size) {
                val distance = junctionBoxes[j].distanceSquared(box)
                if (shortestEdges.size >= connectionLimit) {
                    // Keep the set of connections the required size and
                    // only mutate (remove and add) when a shorter connection is found.
                    if (distance < shortestEdges.peek().distanceSquared) {
                        shortestEdges.poll()
                        shortestEdges.add(Connection(index, j, distance))
                    }
                } else {
                    shortestEdges.add(Connection(index, j, distance))
                }
            }
        }
        return shortestEdges
    }

    /**
     * Builds a list of [Connection]s. These connections are sorted by length.
     * At most [connectionLimit] many connections may be built for each [JunctionBox]
     *
     * @param junctionBoxes the junction boxes to build the connections for
     * @param connectionLimit the maximum amount of connections per junction box to keep
     */
    private fun buildHeuristicConnections(
        junctionBoxes: List<JunctionBox>,
        connectionLimit: Int,
    ): List<Connection> {
        return buildList {
            // Connections between junction boxes as a max heap.
            val shortestConnections = PriorityQueue<Connection>(junctionBoxes.size * connectionLimit)

            // Loop over all but the last junction box
            // The last one will be connected by and to all prior ones
            for (fromIndex in 0 until junctionBoxes.lastIndex) {
                val from = junctionBoxes[fromIndex]

                shortestConnections.clear()

                // Fill the connection set up to the connection limit
                // +1 to skip connections from the junction box to itself
                for (toIndex in fromIndex + 1 until minOf(fromIndex + connectionLimit, junctionBoxes.size)) {
                    val other = junctionBoxes[toIndex]
                    val distance = from.distanceSquared(other)
                    shortestConnections.add(Connection(fromIndex, toIndex, distance))
                }

                // Calculate the remaining distances
                for (toIndex in fromIndex + connectionLimit + 1 until junctionBoxes.size) {
                    val to = junctionBoxes[toIndex]
                    val distance = from.distanceSquared(to)
                    if (distance < shortestConnections.peek().distanceSquared) {
                        // Keep the set of connections the required size and
                        // only mutate (remove and add) when a shorter connection is found.
                        shortestConnections.poll()
                        shortestConnections.add(Connection(fromIndex, toIndex, distance))
                    }
                }
                addAll(shortestConnections)
            }

            // Sort by shortest length first
            sortWith { c1, c2 -> c2.compareTo(c1) }
        }
    }

    /**
     * Assembles and merges the circuits based on the connections given.
     *
     * @param junctionBoxes the list of junction boxes to connect
     * @param connections the connections between the [junctionBoxes]
     * @return an array of the sizes of the circuits, ordered by largest first
     */
    private fun buildCircuitSizes(
        junctionBoxes: List<JunctionBox>,
        connections: Iterable<Connection>
    ): IntArray {
        // Array of circuit ids, beginning with each junction box as their own circuit
        val circuits = IntArray(junctionBoxes.size) { it }

        // Add connections between junction boxes by
        // merging the circuits they are in
        connections.forEach { (box1, box2) ->
            val circuit1 = circuits[box1]
            val circuit2 = circuits[box2]
            if (circuit1 != circuit2) {
                // Merge the circuits
                circuits.replaceAll(circuit2, circuit1)
            }
        }

        val sizes = circuits.boundedFrequencies(junctionBoxes.size)
        sizes.sortDescending()

        return sizes
    }

    private fun buildCompleteCircuit(
        junctionBoxes: List<JunctionBox>,
        connections: List<Connection>
    ): Connection {
        // Array of circuit ids, beginning with each junction box as their own circuit
        val circuits = IntArray(junctionBoxes.size) { it }

        connections.forEach { connection ->
            val (box1, box2) = connection
            val circuit1 = circuits[box1]
            val circuit2 = circuits[box2]
            if (circuit1 != circuit2) {
                // Merge the circuits
                circuits.replaceAll(circuit2, circuit1)
                if (circuits.all { it == circuit1 }) {
                    // We are done, when all circuits are the same
                    return connection
                }
            }
        }
        throw AssertionError()
    }

    private companion object {
        const val NUM_CONNECTIONS_SAMPLE = 10
        const val NUM_CONNECTIONS = 1000

        private fun numberOfConnections(inputFile: String): Int {
            return if (inputFile.endsWith("sample")) {
                NUM_CONNECTIONS_SAMPLE
            } else {
                NUM_CONNECTIONS
            }
        }

        /**
         * A junction box in three-dimensional space
         */
        @JvmRecord
        private data class JunctionBox(
            val x: Int,
            val y: Int,
            val z: Int,
        ) {
            fun distanceSquared(other: JunctionBox): Long {
                val dx = (x - other.x).toLong()
                val dy = (y - other.y).toLong()
                val dz = (z - other.z).toLong()
                return (dx * dx) + (dy * dy) + (dz * dz)
            }
        }

        /**
         * A connection between two [JunctionBox] instances with their distance.
         * When sorted, the longest ones are sorted first.
         */
        @JvmRecord
        private data class Connection(
            val boxIndex1: Int,
            val boxIndex2: Int,
            val distanceSquared: Long,
        ) : Comparable<Connection> {
            override fun compareTo(other: Connection): Int {
                return other.distanceSquared.compareTo(this.distanceSquared)
            }
        }

        /**
         * Count the frequencies of the element of this array.
         * Optimized for sorted arrays.
         * The bounds of [this] is in [0, [upperBound])
         *
         * @receiver the array to count the frequencies for
         * @param upperBound the maximum value of the array to count frequencies for
         */
        private fun IntArray.boundedFrequencies(upperBound: Int): IntArray {
            require(this.isNotEmpty())

            val frequencies = IntArray(upperBound)

            var element = this[0]
            var elementCount = 1

            for (i in indices) {
                val n = this[i]
                if (element == n) {
                    elementCount++
                } else {
                    frequencies[element] += elementCount
                    element = n
                    elementCount = 1
                }
            }
            // Add the last run to the frequencies
            frequencies[element] += elementCount

            return frequencies
        }
    }
}
