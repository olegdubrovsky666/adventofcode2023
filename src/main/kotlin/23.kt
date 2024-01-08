import shared.Position
import java.io.File
import kotlin.math.max

fun start(input: List<String>) = Position(0, input[0].indexOf('.'))
fun end(input: List<String>) = Position(input.indices.last(), input.last().indexOf('.'))

fun canWalk(pos: Position, input: List<String>): Boolean {
    val (row, col) = pos
    return row in input.indices && col in input[row].indices && input[row][col] != '#'
}

private fun part1(input: List<String>): Int {
    val start = start(input)
    val end = end(input)

    val rec = DeepRecursiveFunction<Set<Position>, Int> { path ->
        val pos = path.last()
        if (pos == end) return@DeepRecursiveFunction path.size

        val value = input[pos.row][pos.col]
        val next = when (value) {
            '^' -> listOf(pos.top())
            'v' -> listOf(pos.down())
            '>' -> listOf(pos.right())
            '<' -> listOf(pos.left())
            else -> listOf(pos.top(), pos.down(), pos.left(), pos.right())
        }.filter { canWalk(it, input) && it !in path }

        return@DeepRecursiveFunction if (next.isEmpty()) Int.MIN_VALUE else next.maxOf { callRecursive(path + it) }
    }

    return rec(setOf(start)) - 1
}

data class Node(val pos: Position, val len: Int, val prev: Node?) {
    val next = mutableListOf<Node>()

    fun pathContains(pos: Position): Boolean {
        var cursor: Node? = this
        while (cursor != null) {
            if (cursor.pos == pos) return true
            cursor = cursor.prev
        }

        return false
    }

    fun lengthFromRoot(): Int {
        var result = 0

        var cursor: Node? = this
        while (cursor != null) {
            result += cursor.len
            cursor = cursor.prev
        }

        return result
    }
}

private fun part2(input: List<String>): Int {
    val connectionsCache = hashMapOf<Position, List<Pair<Position, Int>>>()
    fun connections(pos: Position): List<Pair<Position, Int>> {
        return connectionsCache.getOrPut(pos) {
            val connections = mutableListOf<Pair<Position, Int>>()
            val directions = listOf(pos.top(), pos.down(), pos.left(), pos.right())
            for (direction in directions) {
                if (canWalk(direction, input)) {
                    var prev = pos
                    var curr = direction
                    var steps = 0

                    while (true) {
                        steps++
                        val next = listOf(curr.top(), curr.down(), curr.left(), curr.right())
                            .filter { canWalk(it, input) && it != prev }

                        if (next.size == 1) {
                            prev = curr
                            curr = next.first()
                        } else {
                            connections += Pair(curr, steps)
                            break
                        }
                    }
                }
            }

            connections
        }
    }

    val start = start(input)
    val end = end(input)

    val root = Node(pos = start, len = 0, prev = null)
    var result = 0
    var cursor = listOf(root)
    while (cursor.isNotEmpty()) {
        for (node in cursor) {
            val connections = connections(node.pos)
            for ((pos, len) in connections) {
                if (pos == end) {
                    result = max(result, len + node.lengthFromRoot())
                } else if (!node.pathContains(pos)) {
                    node.next += Node(pos, len, node)
                }
            }
        }

        cursor = cursor.flatMap { it.next }
    }

    return result
}

fun main() {
    val input = File("src/main/resources/input/23.txt").readLines()

    println(part1(input))
    println(part2(input))
}