import java.io.File
import kotlin.streams.asStream

private fun part1(input: List<String>): Int {
    val edges = mutableListOf<Pair<String, String>>()
    val connections = hashMapOf<String, HashSet<String>>()
    for (line in input) {
        val components = line.split(": ", " ")
        val left = components.first()
        val right = components.drop(1)

        val leftConnections = connections.getOrPut(left) { hashSetOf() }
        leftConnections += right

        for (r in right) {
            val rConnections = connections.getOrPut(r) { hashSetOf() }
            rConnections += left

            edges += Pair(left, r)
        }
    }

    fun findConnectedNodes(root: String, excludeConnections: List<Pair<String, String>>): Set<String> {
        val seen = hashSetOf(root)
        var cursors = setOf(root)
        while (cursors.isNotEmpty()) {
            cursors = cursors.flatMapTo(hashSetOf()) { cursor ->
                connections[cursor]!!.filter { connection ->
                    connection !in seen
                            && Pair(connection, cursor) !in excludeConnections
                            && Pair(cursor, connection) !in excludeConnections
                }
            }

            seen += cursors
        }
        return seen
    }

    fun shortestPath(
        from: String,
        to: String,
        excludeConnections: List<Pair<String, String>>
    ): List<Pair<String, String>> {
        val seen = hashSetOf<String>()
        var pathes = listOf(listOf(from))
        while (true) {
            val next = mutableListOf<List<String>>()

            for (path in pathes) {
                val cursor = path.last()
                for (connection in connections[cursor]!!) {
                    if (connection in seen
                        || Pair(connection, cursor) in excludeConnections
                        || Pair(cursor, connection) in excludeConnections
                    ) {
                        continue
                    }

                    if (connection == to) return path.zip(path.drop(1) + connection)

                    next += path + connection
                }
            }

            pathes = next
        }
    }

    fun combinations() = sequence {
        for (a in edges) {
            for (b in shortestPath(a.first, a.second, excludeConnections = listOf(a))) {
                for (c in shortestPath(b.first, b.second, excludeConnections = listOf(a, b))) {
                    yield(listOf(a, b, c))
                }
            }
        }
    }


    return combinations().asStream()
        .parallel()
        .map { excludeConnections ->
            val (a, b) = excludeConnections.first()
            val g1 = findConnectedNodes(a, excludeConnections)
            val g2 = findConnectedNodes(b, excludeConnections)
            if (g1 != g2) (g1.size * g2.size) else -1
        }
        .filter { it != -1 }
        .findFirst().get()
}

fun main() {
    val input = File("src/main/resources/input/25.txt").readLines()

    println(part1(input))
}