import java.io.File

private val instructionRegex = "(\\w+)([=|-])(\\w*)".toRegex()

private fun hash(string: String): Int = string.fold(0) { hash, char -> ((hash + char.code) * 17) % 256 }

private fun part1(string: String): Int {
    return string.split(',').sumOf { hash(it) }
}

private fun part2(string: String): Int {
    val boxes = Array<LinkedHashMap<String, Int>>(256) { linkedMapOf() }
    for (instruction in string.split(',')) {
        val (label, operation, length) = instructionRegex.matchEntire(instruction)!!.destructured
        val box = hash(label)
        when (operation) {
            "-" -> boxes[box].remove(label)
            "=" -> boxes[box][label] = length.toInt()
        }
    }

    var result = 0
    for ((box, lenses) in boxes.withIndex()) {
        for ((slot, lens) in lenses.toList().withIndex()) {
            val (label, length) = lens
            result += (box + 1) * (slot + 1) * length
        }
    }

    return result
}

fun main() {
    val (input) = File("src/main/resources/input/15.txt").readLines()

    println(part1(input))
    println(part2(input))
}