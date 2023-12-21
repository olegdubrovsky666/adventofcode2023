import java.io.File

private fun transpose(input: List<String>): List<String> {
    return input[0].indices.map { col ->
        input.indices.map { row -> input[row][col] }.joinToString("")
    }
}

private fun shiftleft(input: String): String {
    return input.split("#").map {
        val sb = StringBuilder()
        val rocks = it.count { c -> c == 'O' }

        repeat(rocks) { sb.append('O') }
        repeat(it.length - rocks) { sb.append('.') }

        sb.toString()
    }.joinToString("#")
}

private fun shiftright(input: String): String {
    return input.split("#").map {
        val sb = StringBuilder()
        val rocks = it.count { c -> c == 'O' }

        repeat(it.length - rocks) { sb.append('.') }
        repeat(rocks) { sb.append('O') }

        sb.toString()
    }.joinToString("#")
}

private fun slideEast(input: List<String>): List<String> {
    return input.map { shiftright(it) }
}

private fun slideWest(input: List<String>): List<String> {
    return input.map { shiftleft(it) }
}

private fun slideNorth(input: List<String>): List<String> {
    return transpose(transpose(input).map { shiftleft(it) })
}

private fun slideSouth(input: List<String>): List<String> {
    return transpose(transpose(input).map { shiftright(it) })
}

private fun cycle(input: List<String>): List<String> {
    return slideEast(slideSouth(slideWest(slideNorth(input))))
}

private fun calculateLoad(input: List<String>): Int {
    return input.withIndex().map { (index, line) ->
        val weight = input.size - index
        val count = line.count { it == 'O' }

        count * weight
    }.sum()
}

private fun part1(input: List<String>): Int {
    return calculateLoad(slideNorth(input))
}

private fun part2(input: List<String>): Int {
    var platform = input
    val seen = mutableListOf(platform)

    for (i in 1..1_000_000_000) {
        platform = cycle(platform)
        val loopDetector = seen.indexOf(platform)

        if (loopDetector != -1) {
            platform = seen[loopDetector + ((1_000_000_000 - i) % (i - loopDetector))]
            break
        }

        seen.add(platform)
    }

    return calculateLoad(platform)
}

fun main() {
    val input = File("src/main/resources/input/14.txt").readLines()

    println(part1(input))
    println(part2(input))
}