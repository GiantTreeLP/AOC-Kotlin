package common

data class Vertex<T>(
    val index: Int,
    val data: T,
)

data class Edge<T>(
    val source: Vertex<T>,
    val destination: Vertex<T>,
    val weight: Double? = null,
)

class AdjacencyList<T> {
    private val adjacencyMap = mutableMapOf<Vertex<T>, ArrayList<Edge<T>>>()

    fun createVertex(data: T): Vertex<T> {
        val vertex = Vertex(adjacencyMap.count(), data)
        adjacencyMap[vertex] = arrayListOf()
        return vertex
    }

    fun addDirectedEdge(source: Vertex<T>, destination: Vertex<T>, weight: Double? = null) {
        val edge = Edge(source, destination, weight)
        adjacencyMap[source]?.let { edges ->
            if (!edges.contains(edge)) {
                edges.add(edge)
            }
        }
    }

    fun getCycle(startVertex: Vertex<T>): Set<Vertex<T>>? {
        require(adjacencyMap.containsKey(startVertex)) { "Vertex not in graph" }

        val visited = mutableSetOf<Vertex<T>>()
        val stack = mutableSetOf<Vertex<T>>()

        fun hasCycle(vertex: Vertex<T>): Boolean {
            if (stack.contains(vertex)) return true
            if (visited.contains(vertex)) return false

            visited.add(vertex)
            stack.add(vertex)

            val edges = adjacencyMap[vertex] ?: emptyList()
            for (edge in edges) {
                if (hasCycle(edge.destination)) {
                    return true
                }
            }

            stack.remove(vertex)
            return false
        }

        return if (hasCycle(startVertex)) visited else null
    }

    fun pruneGraph() {
        val verticesToRemove = adjacencyMap.filter { it.value.isEmpty() }.keys
        adjacencyMap.keys.removeAll(verticesToRemove)
        adjacencyMap.values.forEach { it.removeAll { edge -> verticesToRemove.contains(edge.destination) } }
    }

    fun getVertices(): List<Vertex<T>> = adjacencyMap.keys.toList()

    fun getEdges(vertex: Vertex<T>): List<Edge<T>> = adjacencyMap[vertex] ?: emptyList()

    override fun toString(): String {
        return buildString {
            adjacencyMap.forEach { (vertex, edges) ->
                val edgeString = edges.joinToString { it.destination.data.toString() }
                append("${vertex.data} -> [$edgeString]\n")
            }
        }
    }
}
