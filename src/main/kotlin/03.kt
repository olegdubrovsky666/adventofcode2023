import shared.Position
import java.io.File

data class PartNumber(val row: Int, val start: Int, val end: Int, val value: Int)

private fun neighbours(position: Position, input: List<String>): List<Position> {
    return listOf(
        Position(position.row - 1, position.col - 1),
        Position(position.row - 1, position.col),
        Position(position.row - 1, position.col + 1),
        Position(position.row, position.col - 1),
        Position(position.row, position.col + 1),
        Position(position.row + 1, position.col - 1),
        Position(position.row + 1, position.col),
        Position(position.row + 1, position.col + 1),
    ).filter { (row, col) -> row in input.indices && col in input[row].indices }
}

fun neighbourPartNumbers(position: Position, input: List<String>): List<PartNumber> {
    return neighbours(position, input)
        .filter { (row, col) -> input[row][col].isDigit() }
        .map { (row, col) ->
            var start = col
            var end = col

            while (start > 0 && input[row][start - 1].isDigit()) start--
            while (end < input[row].length && input[row][end].isDigit()) end++

            PartNumber(row, start, end, input[row].substring(start, end).toInt())
        }.distinct()
}


private fun part1(input: List<String>): Int {
    return input.withIndex().flatMap { (row, line) ->
        line.withIndex()
            .filter { (_, char) -> !char.isDigit() && !char.equals('.') }
            .flatMap { (col, _) -> neighbourPartNumbers(Position(row, col), input) }
    }.distinct().sumOf { it.value }
}

private fun part2(input: List<String>): Int {
    var result = 0

    input.forEachIndexed { row, line ->
        line.forEachIndexed { col, char ->
            if (char.equals('*')) {
                val neighbourPartNumbers = neighbourPartNumbers(Position(row, col), input)
                if (neighbourPartNumbers.size == 2) {
                    result += neighbourPartNumbers.map { it.value }.reduce { a, b -> a * b }
                }
            }
        }
    }

    return result
}

fun main() {
    val input = File("src/main/resources/input/03.txt").readLines()
    println(part1(input))
    println(part2(input))
}