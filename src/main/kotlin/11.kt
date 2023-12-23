import shared.Position
import java.io.File


private fun galaxies(input: List<String>): List<Position> {
    val galaxies = mutableListOf<Position>()

    for (row in input.indices) {
        for (col in input[row].indices) {
            if (input[row][col] == '#') {
                galaxies.addLast(Position(row, col))
            }
        }
    }

    return galaxies.toList()
}

private fun sumOfPathsBetweenGalaxies(galaxies: List<Position>, scalingFactor: Int): Long {
    val rowsWithGalaxies = galaxies.groupBy { it.row }.keys
    val colsWIthGalaxies = galaxies.groupBy { it.col }.keys


    var result = 0L
    for (a in 0..<galaxies.size) {
        for (b in (a + 1)..<galaxies.size) {
            val first = galaxies[a]
            val second = galaxies[b]

            val rowsBetween = if (first.row < second.row) first.row..second.row else second.row..first.row
            val colsBetween = if (first.col < second.col) first.col..second.col else second.col..first.col

            val emptyRowsBetweenGalaxies = rowsBetween.count { it !in rowsWithGalaxies }
            val emptyColsBetweenGalaxies = colsBetween.count { it !in colsWIthGalaxies }

            val path =
                (rowsBetween.count() - 1) + (colsBetween.count() - 1) + (emptyRowsBetweenGalaxies * (scalingFactor - 1)) + (emptyColsBetweenGalaxies * (scalingFactor - 1))


            result += path
        }
    }

    return result
}

private fun part1(galaxies: List<Position>): Long = sumOfPathsBetweenGalaxies(galaxies, 2)
private fun part2(galaxies: List<Position>): Long = sumOfPathsBetweenGalaxies(galaxies, 1_000_000)

fun main() {
    val input = File("src/main/resources/input/11.txt").readLines()
    val galaxies = galaxies(input)

    println(part1(galaxies))
    println(part2(galaxies))
}