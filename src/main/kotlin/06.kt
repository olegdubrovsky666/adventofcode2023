import java.io.File

data class Race(val time: Long, val distance: Long)

fun parseInputForPart1(input: List<String>): List<Race> {
    val (timesString, distancesString) = input

    val times = timesString.removePrefix("Time: ").split(" ").filter { it.isNotBlank() }.map { it.toLong() }
    val distances = distancesString.removePrefix("Distance: ").split(" ").filter { it.isNotBlank() }.map { it.toLong() }

    return times.zip(distances).map { (time, distance) -> Race(time, distance) }
}

fun parseInputForPart2(input: List<String>): Race {
    val (timeString, distanceString) = input

    val time = timeString.removePrefix("Time: ").replace(" ", "").toLong()
    val distance = distanceString.removePrefix("Distance: ").replace(" ", "").toLong()

    return Race(time, distance)
}

fun countOfWaysToWin(race: Race): Long {
    for (chargeTime in 1..<race.time) {
        if (chargeTime * (race.time - chargeTime) > race.distance) {
            return race.time - chargeTime - chargeTime + 1
        }
    }
    return 0
}

private fun part1(input: List<String>): Long {
    val races = parseInputForPart1(input)

    return races.map { race -> countOfWaysToWin(race) }.reduce { a, b -> a * b }
}

private fun part2(input: List<String>): Long {
    val race = parseInputForPart2(input)

    return countOfWaysToWin(race)
}

fun main() {
    val input = File("src/main/resources/input/06.txt").readLines()

    println(part1(input))
    println(part2(input))
}