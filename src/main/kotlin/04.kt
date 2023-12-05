import java.io.File

data class Card(val playNumbers: List<Int>, val winningNumbers: List<Int>)

private fun parseCard(str: String): Card {
    val (_, numbers) = str.split(":")
    val (winningNumbers, playNumbers) = numbers.split("|")

    return Card(
        winningNumbers = winningNumbers.split(" ").filter { it.isNotBlank() }.map { it.toInt() },
        playNumbers = playNumbers.split(" ").filter { it.isNotBlank() }.map { it.toInt() }
    )
}

private fun part1(cards: List<Card>): Int {
    return cards
        .map { card -> card.playNumbers.intersect(card.winningNumbers) }
        .filter { wonNumbers -> wonNumbers.isNotEmpty() }
        .map { wonNumbers -> 1.shl(wonNumbers.size - 1) }
        .sum()

}

private fun part2(cards: List<Card>): Int {
    val instances = IntArray(cards.size) { 1 }

    cards.forEachIndexed { index, card ->
        val instanceCount = instances[index]
        val wonNumbersCount = card.playNumbers.intersect(card.winningNumbers).size

        for (offset in 1..wonNumbersCount) {
            if (index + offset < instances.size) {
                instances[index + offset] += instanceCount
            }
        }
    }

    return instances.sum()
}

fun main() {
    val input = File("src/main/resources/input/04.txt").readLines()
    val cards = input.map { line -> parseCard(line) }
    println(part1(cards))
    println(part2(cards))
}