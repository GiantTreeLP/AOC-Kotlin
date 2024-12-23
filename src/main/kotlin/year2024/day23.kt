package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceLines

@AutoService(AOCSolution::class)
class Day23 : AOCSolution {
    override val year = 2024
    override val day = 23

    private fun bronKerbosch(
        graph: List<List<Int>>,
        r: List<Int>,
        p: MutableSet<Int>,
        x: MutableSet<Int>,
    ): List<List<Int>> {
        if (p.isEmpty() && x.isEmpty()) {
            return listOf(r.toList())
        }

        val result = mutableListOf<List<Int>>()

        if (p.isEmpty()) {
            return result
        }

        val pCopy = p.toMutableSet()
        pCopy.removeAll(graph[p.first()])

        pCopy.forEach { v ->
            val neighbors = graph[v]
            result.addAll(
                bronKerbosch(
                    graph,
                    r + v,
                    p.toMutableSet().apply {
                        retainAll(neighbors)
                    },
                    x.toMutableSet().apply {
                        retainAll(neighbors)
                    }
                )
            )
            p -= v
            x += v
        }

        return result
    }

    private fun parseConnections(inputFile: String): Pair<List<List<Int>>, Map<String, Int>> {
        val graph = mutableListOf<MutableList<Int>>()
        val computerIds = mutableMapOf<String, Int>()

        readResourceLines(inputFile).forEach {
            val (from, to) = it.split("-")
            val fromGraph = computerIds.getOrPut(from) {
                graph.add(mutableListOf())
                graph.size - 1
            }
            val toGraph = computerIds.getOrPut(to) {
                graph.add(mutableListOf())
                graph.size - 1
            }
            graph[fromGraph].add(toGraph)
            graph[toGraph].add(fromGraph)
        }

        return graph to computerIds
    }

    override fun part1(inputFile: String): String {
        val (graph, computerIds) = parseConnections(inputFile)

        val triples = buildSet {
            computerIds
                .filter { it.key[0] == 't' }
                .forEach { (_, relevantComputer) ->
                    graph[relevantComputer].withIndex().drop(1).forEach { (i, secondComputer) ->
                        graph[relevantComputer].take(i).forEach { thirdComputer ->
                            if (graph[secondComputer].contains(thirdComputer)) {
                                add(setOf(relevantComputer, secondComputer, thirdComputer))
                            }
                        }

                    }
                }
        }

        return triples.size.toString()
    }

    override fun part2(inputFile: String): String {
        val (graph, computerIds) = parseConnections(inputFile)

        val computerNames = computerIds.entries.associate { it.value to it.key }

        val maximalClique = bronKerbosch(
            graph,
            emptyList(),
            computerIds.values.toMutableSet(),
            mutableSetOf()
        )
            .maxBy { it.size }
            .map { computerNames.getValue(it) }
            .sorted()
            .joinToString(",")

        return maximalClique
    }
}