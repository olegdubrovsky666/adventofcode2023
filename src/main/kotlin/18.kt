import shared.math.shoelace
import java.io.File
import java.math.BigDecimal

typealias Coordinate = Pair<Int, Int>
typealias Instruction = Pair<Char, Int>

private fun coords(instructions: List<Instruction>): List<Coordinate> {
    val coords = instructions.fold(listOf(Pair(0, 0))) { coords, (direction, offset) ->
        val (x, y) = coords.last()

        val next = when (direction) {
            'R' -> Pair(x + offset, y)
            'L' -> Pair(x - offset, y)
            'U' -> Pair(x, y + offset)
            'D' -> Pair(x, y - offset)

            else -> throw Exception("Unknown direction")
        }

        coords + next
    }


    return coords.toList()
}

private fun clockwise(instructions: List<Instruction>): Int {
    var result = 0
    var a = instructions.last()
    for (b in instructions) {
        val (adirection) = a
        val (bdirection) = b

        if ((adirection == 'R' && bdirection == 'D')
            || (adirection == 'D' && bdirection == 'L')
            || (adirection == 'L' && bdirection == 'U')
            || (adirection == 'U' && bdirection == 'R')
        ) result++

        a = b
    }
    return result
}

private fun area(instructions: List<Instruction>): BigDecimal {
    val coords = coords(instructions)
    val perimeter = instructions.sumOf { (direction, offset) -> BigDecimal(offset) }
    val corners = instructions.count()
    val adjustment = (perimeter * BigDecimal(2) + BigDecimal((2 * clockwise(instructions)) - corners)) / BigDecimal(4)
    return shoelace(coords) + adjustment
}


private fun part1(input: List<String>): BigDecimal {
    val instructions = input.map { line ->
        val direction = line.first()
        val offset = line.drop(2).takeWhile { it.isDigit() }.toInt()

        Pair(direction, offset)
    }

    return area(instructions)
}

@OptIn(ExperimentalStdlibApi::class)
private fun part2(input: List<String>): BigDecimal {
    val instructions = input.map {
        val colorCode = it.split(" ").last().drop(2).dropLast(1)
        val length = colorCode.take(5).hexToInt()
        val direction = when (colorCode.last()) {
            '0' -> 'R'
            '1' -> 'D'
            '2' -> 'L'
            '3' -> 'U'

            else -> throw Exception("Unknown direction")
        }

        Pair(direction, length)
    }

    return area(instructions)
}

fun main() {
    val input = File("src/main/resources/input/18.txt").readLines()

    println(part1(input))
    println(part2(input))
}