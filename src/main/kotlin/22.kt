import java.io.File

private data class Brick(var x: IntRange, var y: IntRange, var z: IntRange)

private fun bricks(input: List<String>): Map<Int, Brick> {
    return input.map { line ->
        val (from, to) = line.split('~').map {
            it.split(',').map { it.toInt() }
        }
        val (x, y, z) = from.zip(to)
        val (x1, x2) = x
        val (y1, y2) = y
        val (z1, z2) = z

        Brick(x1..x2, y1..y2, z1..z2)
    }.withIndex().associate { it.index + 1 to it.value }
}

private fun array3d(bricks: Map<Int, Brick>): Array<Array<Array<Int>>> {
    val maxX = bricks.values.maxOf { it.x.max() }
    val maxY = bricks.values.maxOf { it.y.max() }
    val maxZ = bricks.values.maxOf { it.z.max() }

    val arr = Array(maxZ + 1) {
        Array(maxX + 1) {
            Array(maxY + 1) {
                0
            }
        }
    }

    for ((i, brick) in bricks) {
        for (x in brick.x) {
            for (y in brick.y) {
                for (z in brick.z) {
                    arr[z][x][y] = i
                }
            }
        }
    }

    return arr
}

private fun shift(arr: Array<Array<Array<Int>>>, bricks: Map<Int, Brick>): Unit {
    do {
        var shifted = 0
        for ((i, brick) in bricks) {
            val minz = brick.z.min()
            if (minz == 0) continue

            var canShift = true
            for (x in brick.x) {
                for (y in brick.y) {
                    if (arr[minz - 1][x][y] != 0) canShift = false
                }
            }
            if (!canShift) continue

            shifted++
            for (x in brick.x) {
                for (y in brick.y) {
                    for (z in brick.z) {
                        arr[z][x][y] = 0
                        arr[z - 1][x][y] = i
                    }
                }
            }
            brick.z = (brick.z.min() - 1)..(brick.z.max() - 1)
        }
    } while (shifted != 0)
}

private fun supported(arr: Array<Array<Array<Int>>>, bricks: Map<Int, Brick>): Map<Int, Set<Int>> {
    val supported = bricks.keys.associateWith { mutableSetOf<Int>() }
    for ((i, brick) in bricks) {
        val maxz = brick.z.max()

        if (maxz + 1 in arr.indices) {
            for (x in brick.x) {
                for (y in brick.y) {
                    val upper = arr[maxz + 1][x][y]
                    if (upper != 0) {
                        supported[i]!! += upper
                    }
                }
            }
        }
    }
    return supported
}

private fun supporters(arr: Array<Array<Array<Int>>>, bricks: Map<Int, Brick>): Map<Int, Set<Int>> {
    val supporters = bricks.keys.associateWith { mutableSetOf<Int>() }
    for ((i, brick) in bricks) {
        val minz = brick.z.min()

        if (minz - 1 in arr.indices) {
            for (x in brick.x) {
                for (y in brick.y) {
                    val lower = arr[minz - 1][x][y]
                    if (lower != 0) {
                        supporters[i]!! += lower
                    }
                }
            }
        }
    }
    return supporters
}

private fun part1(bricks: Map<Int, Brick>): Int {
    val arr = array3d(bricks)
    shift(arr, bricks)
    val supported = supported(arr, bricks)
    val supporters = supporters(arr, bricks)

    var result = 0
    for ((i, brick) in bricks) {
        val supportedBricks = supported[i]!!

        var canDisintegrate = true
        for (supportedBrick in supportedBricks) {
            if (supporters[supportedBrick]!!.size == 1) canDisintegrate = false
        }

        if (canDisintegrate) result++
    }
    return result
}

private fun part2(bricks: Map<Int, Brick>): Int {
    val arr = array3d(bricks)
    shift(arr, bricks)
    val supported = supported(arr, bricks)
    val supporters = supporters(arr, bricks)

    var result = 0
    for ((i, brick) in bricks) {
        var cursor = setOf(i)
        var fallen = setOf<Int>()
        while (cursor.isNotEmpty()) {
            var next = setOf<Int>()
            for (supporter in cursor) {
                val supportedBricks = supported[supporter]!!
                for (supportedBrick in supportedBricks) {
                    if (supporters[supportedBrick]!!.all { it in cursor || it in fallen }) {
                        next += supportedBrick
                    }
                }
            }

            fallen += next
            cursor = next
        }
        result += fallen.size
    }
    return result
}

fun main() {
    val input = File("src/main/resources/input/22.txt").readLines()
    val bricks = bricks(input)

    println(part1(bricks))
    println(part2(bricks))
}