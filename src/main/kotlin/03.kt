import java.io.File


data class Point(val row: Int, val col: Int, val value: Char)
data class PartNumber(val row: Int, val start: Int, val end: Int, val value: Int)


private fun neighbours(row: Int, col: Int, input: List<String>): List<Point> {
    return listOf(
        Pair(row - 1, col - 1),
        Pair(row - 1, col),
        Pair(row - 1, col + 1),
        Pair(row, col - 1),
        Pair(row, col + 1),
        Pair(row + 1, col - 1),
        Pair(row + 1, col),
        Pair(row + 1, col + 1),
    ).filter { (_row, _col) -> _row >= 0 && _col >= 0 && _row < input.size && col < input[_row].length }
        .map { (_row, _col) -> Point(_row, _col, input[_row][_col]) }
}

fun neighbourPartNumbers(row: Int, col: Int, input: List<String>): List<PartNumber> {
    return neighbours(row, col, input)
        .filter { it.value.isDigit() }
        .map {
            var start = it.col
            var end = it.col

            while (start > 0 && input[it.row][start - 1].isDigit()) start--
            while (end < input[it.row].length && input[it.row][end].isDigit()) end++

            PartNumber(it.row, start, end, input[it.row].substring(start, end).toInt())
        }.distinct()
}


private fun part1(input: List<String>): Int {
    return input.withIndex().flatMap { (row, line) ->
        line.withIndex()
            .filter { (_, char) -> !char.isDigit() && !char.equals('.') }
            .flatMap { (col, _) -> neighbourPartNumbers(row, col, input) }
    }.distinct().sumOf { it.value }
}

private fun part2(input: List<String>): Int {
    var result = 0

    input.forEachIndexed { row, line ->
        line.forEachIndexed { col, char ->
            if (char.equals('*')) {
                val neighbourPartNumbers = neighbourPartNumbers(row, col, input)
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



