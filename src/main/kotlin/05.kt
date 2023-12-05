import java.io.File
import kotlin.math.max


data class RangeMap(val sourceRange: LongRange, val offset: Long)
data class ApplyRangeMapResult(val result: List<LongRange>, val unmatched: List<LongRange>)
data class Almanac(
    val seeds: List<Long> = emptyList(),
    val seedToSoil: List<RangeMap> = emptyList(),
    val soilToFertilizer: List<RangeMap> = emptyList(),
    val fertilizerToWater: List<RangeMap> = emptyList(),
    val waterToLight: List<RangeMap> = emptyList(),
    val lightToTemperature: List<RangeMap> = emptyList(),
    val temperatureToHumidity: List<RangeMap> = emptyList(),
    val humidityToLocation: List<RangeMap> = emptyList()
)


private fun parseRangeMap(line: String): RangeMap {
    val (destRangeStart, sourceRangeStart, rangeLength) = line.split(" ")

    return RangeMap(
        sourceRange = sourceRangeStart.toLong()..<sourceRangeStart.toLong() + rangeLength.toLong(),
        offset = destRangeStart.toLong() - sourceRangeStart.toLong()
    )
}

private fun parseAlmanac(input: List<String>): Almanac {
    var almanac = Almanac()

    val seeds = input[0].removePrefix("seeds: ").split(" ").map { it.toLong() }
    almanac = almanac.copy(seeds = seeds)

    var start = 2
    var end = start
    while (end < input.size) {
        while (end < input.size && input[end].isNotBlank()) end++

        val name = input[start]
        val rangeMaps = input.subList(start + 1, end).map { line -> parseRangeMap(line) }

        when (name) {
            "seed-to-soil map:" -> almanac = almanac.copy(seedToSoil = rangeMaps)
            "soil-to-fertilizer map:" -> almanac = almanac.copy(soilToFertilizer = rangeMaps)
            "fertilizer-to-water map:" -> almanac = almanac.copy(fertilizerToWater = rangeMaps)
            "water-to-light map:" -> almanac = almanac.copy(waterToLight = rangeMaps)
            "light-to-temperature map:" -> almanac = almanac.copy(lightToTemperature = rangeMaps)
            "temperature-to-humidity map:" -> almanac = almanac.copy(temperatureToHumidity = rangeMaps)
            "humidity-to-location map:" -> almanac = almanac.copy(humidityToLocation = rangeMaps)
        }

        start = end + 1
        end = start
    }

    return almanac
}


private fun getDestination(source: Long, rangeMaps: List<RangeMap>): Long {
    for (rangeMap in rangeMaps) {
        if (rangeMap.sourceRange.contains(source)) {
            val result = source + rangeMap.offset
            return result
        }
    }

    return source
}

private fun mergeRanges(ranges: List<LongRange>): List<LongRange> {
    var changed: Boolean
    val ordered = ranges.distinct().sortedBy { it.first }.toMutableList()

    do {
        changed = false
        var mergedRange: LongRange? = null
        var mergedRangePosition: Int? = null
        for ((index, a) in ordered.withIndex()) {
            if (index == ordered.size - 1) {
                continue
            }

            val b = ordered[index + 1]

            if (b.first - a.last <= 1) {
                changed = true
                mergedRange = a.first..max(a.last, b.last)
                mergedRangePosition = index
            }
        }

        if (changed && mergedRangePosition != null && mergedRange != null) {
            ordered.removeAt(mergedRangePosition)
            ordered[mergedRangePosition] = mergedRange
        }
    } while (changed)

    return ordered.toList()
}

private fun applyRangeMap(range: LongRange, rangeMap: RangeMap): ApplyRangeMapResult {
    val a = range.first
    val b = range.last

    val x = rangeMap.sourceRange.first
    val y = rangeMap.sourceRange.last
    val offset = rangeMap.offset


    return when {
        a < x && (x..y).contains(b) -> ApplyRangeMapResult(
            result = listOf(x + offset..b + offset),
            unmatched = listOf(a..<x)
        )

        x <= a && b <= y -> ApplyRangeMapResult(
            result = listOf(a + offset..b + offset),
            unmatched = emptyList()
        )

        (x..y).contains(a) && y < b -> ApplyRangeMapResult(
            result = listOf(a + offset..y + offset),
            unmatched = listOf(y + 1..b)
        )

        a < x && y < b -> ApplyRangeMapResult(
            result = listOf(x + offset..y + offset),
            unmatched = listOf(a..<x, y + 1..b)
        )

        else -> ApplyRangeMapResult(result = emptyList(), unmatched = listOf(a..b))
    }
}

private fun applyRangeMaps(range: LongRange, rangeMaps: List<RangeMap>): List<LongRange> {
    var result: List<LongRange> = listOf()
    var unmatched: List<LongRange> = listOf(range)


    for (rangeMap in rangeMaps) {

        val applyResults = unmatched.map { applyRangeMap(it, rangeMap) }

        result += applyResults.flatMap { it.result }
        unmatched = applyResults.flatMap { it.unmatched }
    }

    return mergeRanges(result + unmatched)
}


private fun part1(almanac: Almanac): Long {
    return almanac.seeds
        .map { seed -> getDestination(seed, almanac.seedToSoil) }
        .map { soil -> getDestination(soil, almanac.soilToFertilizer) }
        .map { fertilizer -> getDestination(fertilizer, almanac.fertilizerToWater) }
        .map { water -> getDestination(water, almanac.waterToLight) }
        .map { light -> getDestination(light, almanac.lightToTemperature) }
        .map { temperature -> getDestination(temperature, almanac.temperatureToHumidity) }
        .map { humidity -> getDestination(humidity, almanac.humidityToLocation) }
        .toList()
        .min()
}

private fun part2(almanac: Almanac): Long {
    val seedRanges = almanac.seeds.chunked(2).map { (start, length) -> start..<start + length }

    val soilRanges = seedRanges.flatMap { applyRangeMaps(it, almanac.seedToSoil) }
    val fertilizerRanges = soilRanges.flatMap { applyRangeMaps(it, almanac.soilToFertilizer) }
    val waterRanges = fertilizerRanges.flatMap { applyRangeMaps(it, almanac.fertilizerToWater) }
    val lightRanges = waterRanges.flatMap { applyRangeMaps(it, almanac.waterToLight) }
    val temperatureRanges = lightRanges.flatMap { applyRangeMaps(it, almanac.lightToTemperature) }
    val humidityRanges = temperatureRanges.flatMap { applyRangeMaps(it, almanac.temperatureToHumidity) }
    val locationRanges = humidityRanges.flatMap { applyRangeMaps(it, almanac.humidityToLocation) }

    return locationRanges.minOf { it.first }
}


fun main() {
    val input = File("src/main/resources/input/05.txt").readLines()
    val almanac = parseAlmanac(input)

    println(part1(almanac))
    println(part2(almanac))
}