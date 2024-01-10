import java.io.File
import java.math.BigDecimal
import java.math.MathContext


data class Position2(val x: BigDecimal, val y: BigDecimal)
data class Vector2(
    val x: BigDecimal,
    val y: BigDecimal,
    val dx: BigDecimal,
    val dy: BigDecimal
) {
    val a = dy.divide(dx, MathContext.DECIMAL128)
    val b = y - (a * x)

    fun fnx(x: BigDecimal) = (a * x) + b

    fun intersection(other: Vector2): Position2? {
        if (a == other.a) return null

        val x = (other.b - b).divide(a - other.a, MathContext.DECIMAL128)
        val y = fnx(x)

        if ((this.x - x).signum() == dx.signum() || ((other.x - x).signum() == other.dx.signum())) return null

        return Position2(x, y)
    }
}

data class Vector3(
    val x: BigDecimal,
    val y: BigDecimal,
    val z: BigDecimal,
    val dx: BigDecimal,
    val dy: BigDecimal,
    val dz: BigDecimal
)

fun part1(vectors: List<Vector3>): Int {
    val range = BigDecimal(200000000000000)..BigDecimal(400000000000000)
    var result = 0
    val vectors2d = vectors.map { Vector2(it.x, it.y, it.dx, it.dy) }
    for (a in 0..<vectors2d.size) {
        for (b in (a + 1)..<vectors2d.size) {
            val intersection = vectors2d[a].intersection(vectors2d[b])
            if (intersection != null) {
                val (x, y) = intersection
                if (x in range && y in range) {
                    result++
                }
            }
        }
    }
    return result
}

fun part2(vectors: List<Vector3>): BigDecimal {
    // there are 2 lines in the input having same z and dz, assuming it will be our starting point z and dz
    val z = BigDecimal(242720827369528)
    val dz = BigDecimal(81)

    val (a, b) = vectors
    val (x1, y1, z1, dx1, dy1, dz1) = a
    val (x2, y2, z2, dx2, dy2, dz2) = b
    val t1 = (z - z1) / (dz1 - dz)
    val t2 = (z - z2) / (dz2 - dz)

    val dx = ((x1 + dx1 * t1) - (x2 + dx2 * t2)) / (t1 - t2)
    val dy = ((y1 + dy1 * t1) - (y2 + dy2 * t2)) / (t1 - t2)

    val x = (x1 + dx1 * t1) - (dx * t1)
    val y = (y1 + dy1 * t1) - (dy * t1)


    // Ensure assumptions are correct
    for (v in vectors) {
        val tx = (x - v.x) / (v.dx - dx)
        val ty = (y - v.y) / (v.dy - dy)
        val tz = if (z == v.z && dz == v.dz) tx else (z - v.z) / (v.dz - dz)
        if (tx != ty || tx != tz) {
            throw Exception("Wrong")
        }
    }

    return x + y + z
}

fun main() {
    val input = File("src/main/resources/input/24.txt").readLines()
    val vectors = input.map {
        val (position, velocity) = it.split(" @ ")
        val (x, y, z) = position.split(", ").map { it.toBigDecimal() }
        val (dx, dy, dz) = velocity.split(", ").map { it.toBigDecimal() }

        Vector3(x, y, z, dx, dy, dz)
    }


    println(part1(vectors))
    println(part2(vectors))
}