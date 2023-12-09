import java.io.File

data class DesertMap(val instructions: String, val destinations: Map<String, Pair<String, String>>)

private fun parseDesertMap(input: List<String>): DesertMap {
    return DesertMap(
        input[0],
        input.drop(2).associate { line ->
            val (source, destinations) = line.split(" = ")

            val (left, right) = destinations.removeSurrounding("(", ")").split(", ")

            source to Pair(left, right)
        }
    )
}

private fun part1(desertMap: DesertMap): Int {
    var i = 0
    var curr = "AAA"
    while (curr != "ZZZ") {
        val (left, right) = desertMap.destinations.get(curr)!!
        curr = if (desertMap.instructions[i % desertMap.instructions.length] == 'L') left else right
        i++
    }
    return i
}

private fun part2(desertMap: DesertMap): Long {
    val points = desertMap.destinations.keys
    val instructions = desertMap.instructions

    // precomputed map which contains destination point and number of steps you need to reach it from a given point and instruction position
    val next = points.associateWith { from ->
        (0L..<instructions.length).associateWith { idx ->
            var i = 0L
            var curr = from
            do {
                val instructionPosition = ((idx + i) % instructions.length).toInt()
                val (left, right) = desertMap.destinations.get(curr)!!
                curr = if (instructions[instructionPosition] == 'L') left else right
                i++
            } while (!curr.endsWith('Z'))

            Pair(curr, i)
        }
    }

    val curr = points
        .filter { it.endsWith('A') }
        .map { next.get(it)!!.get(0L)!! }
        .toTypedArray()

    do {
        for (i in 0..<curr.size) {
            do {
                val (destination, distance) = curr[i]
                val (secondDestination, secondDistance) = curr[(i + 1) % curr.size]

                if (secondDistance > distance) {
                    val (nextDestination, nextDistance) = next.get(destination)!!.get(distance % instructions.length)!!
                    curr[i] = Pair(nextDestination, distance + nextDistance)
                } else {
                    break
                }
            } while (true)

        }

        var allDistancesAreSame = true
        for (i in 0..<curr.size) {
            val (firstDestination, firstDistance) = curr[i]
            val (secondDestination, secondDistance) = curr[(i + 1) % curr.size]

            if (firstDistance != secondDistance) {
                allDistancesAreSame = false
            }
        }
    } while (!allDistancesAreSame)

    val (destination, distance) = curr[0]
    return distance
}

fun main() {
    val input = File("src/main/resources/input/08.txt").readLines()
    val desertMap = parseDesertMap(input)

    println(part1(desertMap))
    println(part2(desertMap))
}