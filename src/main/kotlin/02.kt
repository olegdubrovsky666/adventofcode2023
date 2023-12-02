import java.io.File

data class GameSet(val red: Int = 0, val green: Int = 0, val blue: Int = 0)
data class Game(val id: Int, val sets: List<GameSet>)

fun parseGame(input: String): Game {
    val (title, conf) = input.split(":")
    val id = title.removePrefix("Game ").toInt()

    val sets = conf.split(";").map { setConf ->
        var set = GameSet()

        setConf.split(",").forEach { cubeConf ->
            if (cubeConf.endsWith("red")) {
                val red = cubeConf.removeSurrounding(" ", " red").toInt()
                set = set.copy(red = red)
            } else if (cubeConf.endsWith("green")) {
                val green = cubeConf.removeSurrounding(" ", " green").toInt()
                set = set.copy(green = green)
            } else if (cubeConf.endsWith("blue")) {
                val blue = cubeConf.removeSurrounding(" ", " blue").toInt()
                set = set.copy(blue = blue)
            }
        }

        set
    }
    return Game(id, sets)
}

fun part1(input: List<Game>): Int {
    return input.filter { game ->
        game.sets.all { set ->
            val (red, green, blue) = set

            red <= 12 && green <= 13 && blue <= 14
        }
    }.sumOf { game -> game.id }
}

fun part2(input: List<Game>): Int {
    return input.sumOf { game ->
        game.sets.maxOf { set -> set.red } * game.sets.maxOf { set -> set.green } * game.sets.maxOf { set -> set.blue }
    }
}

fun main() {
    val input = File("src/main/resources/input/02.txt").readLines()
    val games = input.map { line -> parseGame(line) }
    println(part1(games))
    println(part2(games))
}