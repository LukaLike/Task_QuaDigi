package io.github.lukalike

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

const val INTERVAL_SIZE_MINUTES = 5
const val START_OF_SAMPLING = "2017-01-03T09:00:00"

fun main() {
    val measurementData = readMeasurementData()
    val sampledMeasurementData = measurementData.sample(LocalDateTime.parse(START_OF_SAMPLING))
    sampledMeasurementData.outputResults()
}

fun List<Measurement>.sample(startOfSampling: LocalDateTime? = null) =
    this.filterMeasurementsStartingAt(startOfSampling)
        .groupByType()
        .sampleIn5MinuteIntervals()
        .flatten()

private fun List<Measurement>.filterMeasurementsStartingAt(startOfSampling: LocalDateTime? = null) =
    startOfSampling?.let { filter { it.measurementTime > startOfSampling } } ?: this

private fun List<Measurement>.groupByType() = groupBy { it.type }

private fun Map<String, List<Measurement>>.sampleIn5MinuteIntervals() =
    this.map { measurementGroups ->
        measurementGroups.value
            .groupBy { value ->
                val adjustedTime = value.measurementTime.let {
                    if (it.minute % INTERVAL_SIZE_MINUTES == 0 && it.second == 0) {
                        it.minusMinutes(1)
                    } else {
                        it
                    }
                }

                val totalMinutes = ChronoUnit.MINUTES.between(LocalDateTime.MIN, adjustedTime)
                val intervalIndexInMinutes = totalMinutes / INTERVAL_SIZE_MINUTES * INTERVAL_SIZE_MINUTES

                LocalDateTime.MIN.plusMinutes(intervalIndexInMinutes)
            }
            .map { (key, values) ->
                values.maxByOrNull { it.measurementTime }!!
                    .copy(measurementTime = key.plusMinutes(INTERVAL_SIZE_MINUTES * 1L))
            }
            .sortedBy { it.measurementTime }
    }
