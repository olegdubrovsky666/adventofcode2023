package shared

data class Position(val row: Int, val col: Int) {
    fun top(): Position {
        return copy(row = row - 1)
    }

    fun down(): Position {
        return copy(row = row + 1)
    }

    fun left(): Position {
        return copy(col = col - 1)
    }

    fun right(): Position {
        return copy(col = col + 1)
    }
}