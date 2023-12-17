import java.io.File

typealias Pattern = List<String>

private fun readPatterns(input: List<String>): List<Pattern> {
    val patterns = mutableListOf<Pattern>()

    var rest = input
    do {
        val pattern: Pattern = rest.takeWhile { it.isNotBlank() }
        rest = rest.drop(pattern.size + 1)

        patterns += pattern
    } while (rest.isNotEmpty())

    return patterns
}

private fun vdiff(pattern: List<String>, divider: Int): Int {
    return pattern.sumOf { line ->
        val left = line.take(divider)
        val right = line.drop(divider)
        left.reversed().zip(right).count { (l, r) -> l != r }
    }
}

private fun hdiff(pattern: List<String>, divider: Int): Int {
    val top = pattern.take(divider)
    val down = pattern.drop(divider)
    return top.reversed().zip(down).sumOf { (t, d) -> t.zip(d).count { it.first != it.second } }
}

private fun summarize(pattern: List<String>, smudges: Int): Int {
    val verticalDividers =
        (1..<pattern[0].length).filter { verticalDivider -> vdiff(pattern, verticalDivider) == smudges }
    val horizontalDividers =
        (1..<pattern.size).filter { horizontalDivider -> hdiff(pattern, horizontalDivider) == smudges }

    return verticalDividers.sum() + horizontalDividers.sum() * 100
}

private fun part1(patterns: List<Pattern>): Int = patterns.sumOf { summarize(it, 0) }
private fun part2(patterns: List<Pattern>): Int = patterns.sumOf { summarize(it, 1) }

fun main() {
    val input = File("src/main/resources/input/13.txt").readLines()
    val patterns = readPatterns(input)

    println(part1(patterns))
    println(part2(patterns))
}