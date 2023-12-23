import shared.Position
import java.io.File


private fun start(input: Array<CharArray>): Position {
    for (row in 0..<input.size) {
        for (col in 0..<input[row].size) {
            if (input[row][col] == 'S')
                return Position(row = row, col = col)
        }
    }

    throw Exception("The input doesn't contain 'S' symbol")
}

private fun connections(position: Position, input: Array<CharArray>): Pair<Position, Position> {
    val (row, col) = position
    val value = input[row][col]

    return when (value) {
        'F' -> Pair(position.copy(row = row + 1), position.copy(col = col + 1))
        '7' -> Pair(position.copy(row = row + 1), position.copy(col = col - 1))
        'J' -> Pair(position.copy(row = row - 1), position.copy(col = col - 1))
        'L' -> Pair(position.copy(row = row - 1), position.copy(col = col + 1))
        '|' -> Pair(position.copy(row = row + 1), position.copy(row = row - 1))
        '-' -> Pair(position.copy(col = col - 1), position.copy(col = col + 1))

        'S' -> {
            val (a, b) = listOf(
                position.copy(row = position.row + 1),
                position.copy(row = position.row - 1),
                position.copy(col = position.col + 1),
                position.copy(col = position.col - 1)
            ).filter { (row, col) -> row in 0..<input.size && col in 0..<input[row].size }
                .filter { neighbour -> connections(neighbour, input).toList().contains(position) }

            Pair(a, b)
        }

        else -> throw Exception("The element at position ${position} doesn't have any connections")
    }
}

private fun loop(input: Array<CharArray>): List<Position> {
    val start = start(input)
    val loop = mutableListOf(start)

    var (next) = connections(start, input)


    while (next != start) {
        val prev = loop.last()
        loop.addLast(next)
        val (a, b) = connections(next, input)
        next = if (a == prev) b else a
    }

    return loop.toList()
}

private fun part1(input: Array<CharArray>): Int {
    val loop = loop(input)

    return loop.size / 2
}

//returns two possible answers
private fun part2(input: Array<CharArray>): Pair<Int, Int> {
    fun deepmark(arr: Array<CharArray>, pos: Position, value: Char) {
        if (!(pos.row in arr.indices && pos.col in arr[pos.row].indices && arr[pos.row][pos.col] == '.')) {
            return
        }

        arr[pos.row][pos.col] = value
        deepmark(arr, Position(row = pos.row - 1, col = pos.col - 1), value)
        deepmark(arr, Position(row = pos.row - 1, col = pos.col), value)
        deepmark(arr, Position(row = pos.row - 1, col = pos.col + 1), value)

        deepmark(arr, Position(row = pos.row, col = pos.col - 1), value)
        deepmark(arr, Position(row = pos.row, col = pos.col + 1), value)

        deepmark(arr, Position(row = pos.row + 1, col = pos.col - 1), value)
        deepmark(arr, Position(row = pos.row + 1, col = pos.col), value)
        deepmark(arr, Position(row = pos.row + 1, col = pos.col + 1), value)
    }

    val loop = loop(input)
    val seen = Array(input.size) { CharArray(input[it].size) { '.' } }

    for (pos in loop) {
        seen[pos.row][pos.col] = input[pos.row][pos.col]
    }

    var prev = loop.last()
    for (curr in loop) {
        val value = seen[curr.row][curr.col]

        val rowdiff = curr.row - prev.row
        val coldiff = curr.col - prev.col
        val direction = when {
            rowdiff < 0 -> "up"
            rowdiff > 0 -> "down"
            coldiff < 0 -> "left"
            coldiff > 0 -> "right"
            else -> throw Exception("Unable to determine loop direction")
        }

        val topleft = Position(row = curr.row - 1, col = curr.col - 1)
        val top = Position(row = curr.row - 1, col = curr.col)
        val topright = Position(row = curr.row - 1, col = curr.col + 1)
        val right = Position(row = curr.row, col = curr.col + 1)
        val downright = Position(row = curr.row + 1, col = curr.col + 1)
        val down = Position(row = curr.row + 1, col = curr.col)
        val downleft = Position(row = curr.row + 1, col = curr.col - 1)
        val left = Position(row = curr.row, col = curr.col - 1)

        when (value) {
            '-' -> {
                deepmark(seen, top, if (direction == "right") 'A' else 'B')
                deepmark(seen, down, if (direction == "right") 'B' else 'A')
            }

            '|' -> {
                deepmark(seen, left, if (direction == "up") 'A' else 'B')
                deepmark(seen, right, if (direction == "up") 'B' else 'A')
            }

            'F' -> {
                deepmark(seen, left, if (direction == "up") 'A' else 'B')
                deepmark(seen, topleft, if (direction == "up") 'A' else 'B')
                deepmark(seen, top, if (direction == "up") 'A' else 'B')
                deepmark(seen, downright, if (direction == "up") 'B' else 'A')
            }

            '7' -> {
                deepmark(seen, top, if (direction == "right") 'A' else 'B')
                deepmark(seen, topright, if (direction == "right") 'A' else 'B')
                deepmark(seen, right, if (direction == "right") 'A' else 'B')
                deepmark(seen, downleft, if (direction == "right") 'B' else 'A')
            }

            'J' -> {
                deepmark(seen, right, if (direction == "down") 'A' else 'B')
                deepmark(seen, downright, if (direction == "down") 'A' else 'B')
                deepmark(seen, down, if (direction == "down") 'A' else 'B')
                deepmark(seen, topleft, if (direction == "down") 'B' else 'A')
            }

            'L' -> {
                deepmark(seen, down, if (direction == "left") 'A' else 'B')
                deepmark(seen, downleft, if (direction == "left") 'A' else 'B')
                deepmark(seen, left, if (direction == "left") 'A' else 'B')
                deepmark(seen, topright, if (direction == "left") 'B' else 'A')
            }
        }


        prev = curr
    }

    var a = 0
    var b = 0
    for (row in 0..<seen.size) {
        for (col in 0..<seen[row].size) {
            val value = seen[row][col]
            when (value) {
                'A' -> a++
                'B' -> b++
            }
        }
    }

    return Pair(a, b)
}

fun main() {
    val input = File("src/main/resources/input/10.txt")
        .readLines()
        .map { line -> line.toCharArray() }
        .toTypedArray()

    println(part1(input))
    println(part2(input))
}