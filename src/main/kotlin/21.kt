import shared.Position
import java.io.File
import java.util.stream.Collectors
import java.util.stream.Stream

fun isplot(pos: Position, input: List<String>): Boolean {
    val rows = input.size
    val cols = input[0].length

    val (row, col) = pos
    val value = input[(rows + (row % rows)) % rows][(cols + (col % cols)) % cols]
    return value != '#'
}

private fun reachablePlots(steps: Int, input: List<String>): Long {
    for (row in input.indices) {
        for (col in input[row].indices) {
            if (input[row][col] == 'S') {
                val start = Position(row, col)

                var prev = setOf<Position>()
                var curr = setOf(start)

                var result = if (steps % 2 == 0) 1L else 0L
                for (step in 1..steps) {
                    val next = curr.parallelStream()
                        .flatMap { (row, col) ->
                            Stream.of(
                                Position(row - 1, col),
                                Position(row, col + 1),
                                Position(row + 1, col),
                                Position(row, col - 1),
                            )
                        }.filter { it !in prev && isplot(it, input) }
                        .collect(Collectors.toSet())
                    prev = curr
                    curr = next
                    if (step % 2 == steps % 2) {
                        result += curr.size
                    }
                }

                return result
            }
        }
    }

    throw Exception("Unable to find start point")
}

private fun part1(input: List<String>) = reachablePlots(64, input)
private fun part2(input: List<String>) = reachablePlots(26501365, input)

fun main() {
    val input = File("src/main/resources/input/21.txt").readLines()

    println(part1(input))
    println(part2(input))
}