package shared.math

import java.math.BigDecimal

typealias Coordinate = Pair<Int, Int>

fun shoelace(coords: List<Coordinate>): BigDecimal {
    var S2 = BigDecimal(0)

    var a = coords.last()
    for (b in coords) {
        val (ax, ay) = a
        val (bx, by) = b

        S2 += BigDecimal(ax) * BigDecimal(by) - BigDecimal(bx) * BigDecimal(ay)

        a = b
    }

    return S2.abs().divide(BigDecimal(2))
}