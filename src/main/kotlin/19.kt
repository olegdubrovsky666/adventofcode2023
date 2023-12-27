import java.io.File
import java.math.BigDecimal

typealias Part = Map<String, Int>

data class Condition(val key: String, val operator: String, val value: Int) {
    fun eval(part: Part): Boolean {
        return when (operator) {
            "<" -> part[key]!! < value
            ">" -> part[key]!! > value

            else -> throw Exception("Unknown operator")
        }
    }

    fun negate(): Condition {
        return when (operator) {
            "<" -> Condition(key, ">", value - 1)
            ">" -> Condition(key, "<", value + 1)

            else -> throw Exception("Unknown operator")
        }
    }

    fun range(): IntRange {
        return if (operator == "<") 1..(value - 1) else (value + 1)..4000
    }
}

data class Rule(val condition: Condition, val next: String)
data class Workflow(val name: String, val rules: List<Rule>, val default: String)

fun condition(input: String): Condition {
    val key = input.take(1)
    val operator = input.drop(1).take(1)
    val value = input.drop(2).toInt()

    return Condition(key, operator, value)
}

fun workflow(input: String): Workflow {
    val (name, rules, default) = "(?<name>.+)\\{(?<rules>.+),(?<default>[^,]+)}".toRegex()
        .matchEntire(input)?.destructured!!


    return Workflow(
        name,
        rules.split(',').map { rule ->
            val (condition, next) = rule.split(':')
            Rule(condition(condition), next)
        },
        default
    )
}

fun part(input: String): Part {
    return input.removeSurrounding("{", "}")
        .split(',')
        .associate {
            val (key, value) = it.split('=')
            key to value.toInt()
        }
}


private fun part1(workflows: Map<String, Workflow>, parts: List<Part>): Long {
    var result = 0L
    for (part in parts) {
        var workflow = "in"
        do {
            val (name, rules, default) = workflows[workflow]!!

            val match = rules.find { (condition, next) -> condition.eval(part) }
            if (match != null) {
                val (condition, next) = match
                workflow = next
            } else {
                workflow = default
            }
        } while (workflow != "A" && workflow != "R")

        if (workflow == "A") {
            for (value in part.values) {
                result += value
            }
        }
    }

    return result
}

private fun part2(workflows: Map<String, Workflow>): BigDecimal {
    var result = BigDecimal(0)
    fun rec(conditions: List<Condition>, name: String, workflows: Map<String, Workflow>) {
        if (name == "R") {
            return
        }
        if (name == "A") {
            val ranges = listOf("x", "m", "a", "s").associateWith { key -> conditions.filter { it.key == key } }

            result += ranges.mapValues { (key, conditions) ->
                BigDecimal(
                    conditions.map { it.range().toSet() }.fold((1..4000).toSet()) { a, b -> a.intersect(b) }.count()
                )
            }.values.fold(BigDecimal(1)) { a, b -> a * b }
            return
        }

        val workflow = workflows[name]!!

        val negated = mutableListOf<Condition>()
        for ((condition, next) in workflow.rules) {
            rec(conditions + negated + condition, next, workflows)
            negated += condition.negate()
        }
        rec(conditions + negated, workflow.default, workflows)
    }

    rec(emptyList(), "in", workflows)

    return result
}

fun main() {
    val input = File("src/main/resources/input/19.txt").readLines()

    val workflows = input.takeWhile { it.isNotBlank() }.map { workflow(it) }.associateBy { it.name }
    val parts = input.takeLastWhile { it.isNotBlank() }.map { part(it) }

    println(part1(workflows, parts))
    println(part2(workflows))
}