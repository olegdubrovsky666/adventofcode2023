import java.io.File

private fun parseSequences(input: List<String>): List<IntArray> {
    return input.map { line -> line.split(" ").map { it.toInt() }.toIntArray() }
}

private fun nextSequence(sequence: IntArray): IntArray {
    return IntArray(sequence.size - 1) { idx -> sequence[idx + 1] - sequence[idx] }
}

private fun nextValue(sequence: IntArray): Int {
    if (sequence.all { it == 0 }) {
        return 0
    }

    return sequence.last() + nextValue(nextSequence(sequence))
}

private fun prevValue(sequence: IntArray): Int {
    if (sequence.all { it == 0 }) {
        return 0
    }

    return sequence.first() - prevValue(nextSequence(sequence))
}

private fun part1(sequences: List<IntArray>): Int {
    return sequences.map { sequence -> nextValue(sequence) }.sum()
}

private fun part2(sequences: List<IntArray>): Int {
    return sequences.map { sequence -> prevValue(sequence) }.sum()
}

fun main() {
    val input = File("src/main/resources/input/09.txt").readLines()
    val sequences = parseSequences(input)

    println(part1(sequences))
    println(part2(sequences))
}