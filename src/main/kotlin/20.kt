import shared.math.lcm
import java.io.File
import java.util.*

private enum class Pulse { LOW, HIGH }
private class Event(val pulse: Pulse, val from: String, val to: List<String>)


private interface Module {
    val name: String
    val destinations: List<String>
    val queue: Queue<Event>

    fun process(event: Event)
}

private class Broadcaster(
    override val name: String,
    override val destinations: List<String>,
    override val queue: Queue<Event>
) : Module {
    val low = Event(Pulse.LOW, name, destinations)
    val high = Event(Pulse.HIGH, name, destinations)

    override fun process(event: Event) {
        queue.offer(if (event.pulse == Pulse.LOW) low else high)
    }
}

private class FlipFlop(
    override val name: String,
    override val destinations: List<String>,
    override val queue: Queue<Event>
) : Module {
    val low = Event(Pulse.LOW, name, destinations)
    val high = Event(Pulse.HIGH, name, destinations)

    var on = false

    override fun process(event: Event) {
        if (event.pulse == Pulse.LOW) {
            on = !on
            queue.offer(if (on) high else low)
        }
    }
}

private class Conjunction(
    override val name: String,
    override val destinations: List<String>,
    override val queue: Queue<Event>,

    sources: Set<String>
) : Module {
    val low = Event(Pulse.LOW, name, destinations)
    val high = Event(Pulse.HIGH, name, destinations)

    val inputs = sources.associateWith { Pulse.LOW }.toMutableMap()
    override fun process(event: Event) {
        inputs[event.from] = event.pulse
        queue.offer(if (inputs.all { it.value == Pulse.HIGH }) low else high)
    }
}

private fun parse(input: List<String>, queue: Queue<Event>): Map<String, Module> {
    val sources = input.flatMap { line ->
        val (left, right) = line.split(" -> ")
        val from = left.removePrefix("%").removePrefix("&")
        val tos = right.split(", ")

        tos.map { it to from }
    }.groupBy({ (to, from) -> to }, { (to, from) -> from }).mapValues { it.value.toSet() }


    return input.map { line ->
        val (left, right) = line.split(" -> ")
        val name = left.removePrefix("%").removePrefix("&")
        val destinations = right.split(", ")

        if (left.startsWith("%")) {
            FlipFlop(name, destinations, queue)
        } else if (left.startsWith("&")) {
            Conjunction(name, destinations, queue, sources[name]!!)
        } else if (name == "broadcaster") {
            Broadcaster(name, destinations, queue)
        } else {
            throw Exception("Unknown module")
        }
    }.associateBy { it.name }
}

private fun part1(input: List<String>): Int {
    val q = LinkedList<Event>()
    val modules = parse(input, q)
    val initial = Event(from = "button", to = listOf("broadcaster"), pulse = Pulse.LOW)

    var low = 0
    var high = 0
    repeat(1000) {
        q.offer(initial)

        while (q.isNotEmpty()) {
            val event = q.poll()

            if (event.pulse == Pulse.LOW) {
                low += event.to.size
            } else {
                high += event.to.size
            }


            for (destination in event.to) {
                if (destination != "rx") {
                    modules[destination]!!.process(event)
                }
            }
        }

    }

    return low * high
}

private fun part2(input: List<String>): Long {
    val q = LinkedList<Event>()
    val modules = parse(input, q)
    val initial = Event(from = "button", to = listOf("broadcaster"), pulse = Pulse.LOW)

    val rs = modules["rs"] as Conjunction
    val loopDetector = rs.inputs.mapValues { mutableListOf<Long>() }

    var counter = 0L
    while (true) {
        counter++
        q.offer(initial)

        while (q.isNotEmpty()) {
            val event = q.poll()

            for (destination in event.to) {
                if (destination != "rx") {
                    modules[destination]!!.process(event)
                } else {
                    if (event.pulse == Pulse.LOW) {
                        return counter
                    }

                    for ((from, pulse) in rs.inputs) {
                        if (pulse == Pulse.HIGH) {
                            val counters = loopDetector[from]!!
                            val last = counters.lastOrNull()
                            if (last == null || last != counter) {
                                counters += counter
                            }
                        }
                    }

                    if (loopDetector.values.all { it.size > 5 }) {
                        val loop = loopDetector.values.map {
                            val increments = it.dropLast(1).zip(it.drop(1)).map { (a, b) -> b - a }

                            if (increments.distinct().count() == 1) {
                                increments.first()
                            } else {
                                throw Exception("Something is wrong")
                            }
                        }

                        return loop.lcm()
                    }
                }
            }
        }
    }
}


fun main() {
    val input = File("src/main/resources/input/20.txt").readLines()

    println(part1(input))
    println(part2(input))
}