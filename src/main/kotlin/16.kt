import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import shared.Position
import java.io.File

private fun nextPosition(position: Position, direction: String): Position {
    return when (direction) {
        "up" -> position.copy(row = position.row - 1)
        "down" -> position.copy(row = position.row + 1)
        "left" -> position.copy(col = position.col - 1)
        "right" -> position.copy(col = position.col + 1)

        else -> throw Exception("Unknown direction")
    }
}

private fun nextDirections(value: Char, direction: String): List<String> {
    return when (value) {
        '.' -> listOf(direction)
        '\\' -> {
            when (direction) {
                "up" -> listOf("left")
                "down" -> listOf("right")
                "left" -> listOf("up")
                "right" -> listOf("down")

                else -> throw Exception("Unknown direction")
            }
        }

        '/' -> {
            when (direction) {
                "up" -> listOf("right")
                "down" -> listOf("left")
                "left" -> listOf("down")
                "right" -> listOf("up")

                else -> throw Exception("Unknown direction")
            }
        }

        '-' -> {
            when (direction) {
                "left", "right" -> listOf(direction)
                "up", "down" -> listOf("left", "right")

                else -> throw Exception("Unknown direction")
            }
        }

        '|' -> {
            when (direction) {
                "up", "down" -> listOf(direction)
                "left", "right" -> listOf("up", "down")

                else -> throw Exception("Unknown direction")
            }
        }

        else -> throw Exception("Unknown value")
    }
}

private fun energizedCellsCount(start: Position, direction: String, input: List<String>): Int {
    val seen = hashSetOf<Pair<Position, String>>()
    var cursors = listOf(start to direction)
    while (cursors.isNotEmpty()) {
        cursors = cursors.filter { !seen.contains(it) }.onEach { seen.add(it) }.flatMap { (position, direction) ->
            val current = input[position.row][position.col]

            nextDirections(current, direction).map { nextDirection ->
                nextPosition(
                    position,
                    nextDirection
                ) to nextDirection
            }
        }.filter { (position, _) -> position.row in input.indices && position.col in input[position.row].indices }
    }


    return seen.distinctBy { (position, direction) -> position }.count()
}

private fun part1(input: List<String>): Int {
    return energizedCellsCount(Position(0, 0), "right", input)
}

private fun part2(input: List<String>): Int {
    val rows = input.indices
    val cols = input[0].indices

    val results = mutableListOf<Int>()
    runBlocking(Dispatchers.Default) {
        for (row in rows) {
            launch { results += energizedCellsCount(Position(row, cols.first()), "right", input) }
            launch { results += energizedCellsCount(Position(row, cols.last()), "left", input) }
        }
        for (col in cols) {
            launch { results += energizedCellsCount(Position(rows.first(), col), "down", input) }
            launch { results += energizedCellsCount(Position(rows.last(), col), "up", input) }
        }
    }

    return results.max()
}

fun main() {
    val input = File("src/main/resources/input/16.txt").readLines()

    println(part1(input))
    println(part2(input))
}