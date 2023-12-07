import java.io.File

private fun handTypeRank(hand: String): Int {
    return hand.fold(0) { rank, card ->
        rank + hand.count { it == card }
    }
}
private fun handTypeRank2(hand: String): Int {
    return hand.toCharArray().distinct()
        .map{ hand.replace('J', it)}
        .distinct()
        .maxOf { handTypeRank(it) }
}

private fun part1(handAndBid: List<Pair<String, String>>): Long {
    val cardRank = listOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A')
        .withIndex()
        .associate { it.value to it.index }

    return handAndBid.sortedWith { (a, _), (b, _) ->
        val typeComparison = compareValues(handTypeRank(a), handTypeRank(b))

        if (typeComparison == 0) {
            var i = 0
            while (a[i] == b[i]) i++

            compareValues(cardRank.get(a[i]), cardRank.get(b[i]))
        } else {
            typeComparison
        }
    }.foldIndexed(0L) { index, result, (_, bid) ->
        result + ((index + 1) * bid.toLong())
    }
}

private fun part2(handAndBid: List<Pair<String, String>>): Long {
    val cardRank = listOf('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A')
        .withIndex()
        .associate { it.value to it.index }

    return handAndBid.sortedWith { (a, _), (b, _) ->
        val typeComparison = compareValues(handTypeRank2(a), handTypeRank2(b))

        if (typeComparison == 0) {
            var i = 0
            while (a[i] == b[i]) i++

            compareValues(cardRank.get(a[i]), cardRank.get(b[i]))
        } else {
            typeComparison
        }
    }.foldIndexed(0L) { index, result, (_, bid) ->
        result + ((index + 1) * bid.toLong())
    }
}

fun main() {
    val input = File("src/main/resources/input/07.txt").readLines()
    val handAndBid = input.map { line ->
        val (hand, bid) = line.split(" ")
        Pair(hand, bid)
    }

    println(part1(handAndBid))
    println(part2(handAndBid))
}