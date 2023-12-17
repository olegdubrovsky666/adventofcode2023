import java.io.File

data class Record(val springs: String, val groups: List<Int>)

private fun parseRecords(input: List<String>): List<Record> {
    return input.map { line ->
        val (springs, groupsString) = line.split(' ')
        val groups = groupsString.split(',').map { it.toInt() }

        Record(springs, groups)
    }
}

private fun unfold(record: Record): Record {
    return record.copy(
        springs = (1..5).map { record.springs }.joinToString("?"),
        groups = (1..5).flatMap { record.groups }
    )
}

private val cache = hashMapOf<Pair<String, List<Int>>, Long>()

private fun combinations(springs: String, groups: List<Int>): Long {
    return cache.getOrPut(springs to groups) {
        when (springs.firstOrNull()) {
            '.' -> combinations(springs.drop(1), groups)
            '?' -> combinations(springs.drop(1), groups) + combinations('#' + springs.drop(1), groups)
            '#' -> if (groups.isEmpty()) 0 else {
                val group = groups.first()
                val seq = springs.take(group)
                val next = springs.drop(group)

                if (seq.length != groups.first() || seq.contains('.') || next.startsWith('#')) 0 else {
                    //drop first char from next in else branch because it is either a '.' or '?' which can only be replaced with '.'
                    combinations(next.drop(1), groups.drop(1))
                }
            }
            //hit when springs is empty
            else -> if (groups.isEmpty()) 1 else 0
        }
    }
}

private fun part1(records: List<Record>): Long {
    return records
        .map { combinations(it.springs, it.groups) }
        .sum()
}

private fun part2(records: List<Record>): Long {
    return records
        .map { unfold(it) }
        .map { combinations(it.springs, it.groups) }
        .sum()
}

fun main() {
    val input = File("src/main/resources/input/12.txt").readLines()
    val records = parseRecords(input)

    println(part1(records))
    println(part2(records))
}