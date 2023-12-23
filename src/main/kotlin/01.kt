import java.io.File


val digits = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
    .withIndex()
    .associateBy({ it.value }, { it.index + 1 })


private fun part1(input: List<String>): Int {
    return input.fold(0) { acc: Int, line: String ->
        var first = 0
        while (!line[first].isDigit()) {
            first++
        }

        var last = line.length - 1
        while (!line[last].isDigit()) {
            last--
        }

        acc + line[first].digitToInt() * 10 + line[last].digitToInt()
    }
}


private fun part2(input: List<String>): Int {
    return input.fold(0) { acc: Int, line: String ->
        var lineCalibrationValue = 0

        var first = 0
        while (first < line.length) {
            if (line[first].isDigit()) {
                lineCalibrationValue += line[first].digitToInt() * 10
                break
            } else {
                val digit = digits.entries.firstOrNull { (s, _) -> line.substring(first).startsWith(s) }?.value
                if (digit != null) {
                    lineCalibrationValue += digit * 10
                    break
                }
            }
            first++
        }

        var last = line.length - 1
        while (last >= 0) {
            if (line[last].isDigit()) {
                lineCalibrationValue += line[last].digitToInt()
                break
            } else {
                val digit = digits.entries.firstOrNull { (s, _) -> line.substring(0, last + 1).endsWith(s) }?.value
                if (digit != null) {
                    lineCalibrationValue += digit
                    break
                }
            }
            last--
        }

        acc + lineCalibrationValue
    }
}


fun main() {
    val input = File("src/main/resources/input/01.txt").readLines()
    println(part1(input))
    println(part2(input))
}