package shared.math

import kotlin.math.abs


fun lcm(a: Long, b: Long) = abs(a * (b / gcd(a, b)))

fun Iterable<Long>.lcm(): Long = when (this.count()) {
    0 -> 0
    1 -> this.first()
    else -> this.reduce { a, b -> lcm(a, b) }
}