package io.github.lukalike.util

import io.github.lukalike.sampler.Measurement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.collections.Map

@OptIn(ExperimentalCoroutinesApi::class)
fun List<Measurement>.sample(
    intervalSize: Int = INTERVAL_SIZE_MINUTES,
    startOfSampling: LocalDateTime? = LocalDateTime.parse(START_OF_SAMPLING)
): List<Measurement> = runBlocking {
    this@sample
        .groupByType()
        .flatMapMerge { entry ->
            flow {
                val result = async {
                    val filteredMeasurements = entry.value.filterMeasurementsStartingAt(startOfSampling)
                    filteredMeasurements.sampleInIntervals(intervalSize)
                }

                emit(result.await())
            }
        }
        .fold(mutableListOf<Measurement>()) { accumulator, measurements ->
            accumulator.addAll(measurements)
            accumulator
        }
}

private fun List<Measurement>.groupByType() = flow {
    groupBy { it.type }
        .entries
        .forEach { emit(it) }
}

private fun List<Measurement>.filterMeasurementsStartingAt(startOfSampling: LocalDateTime?) =
    startOfSampling?.let { filter { it.measurementTime >= startOfSampling } } ?: this

private fun List<Measurement>.sampleInIntervals(intervalSize: Int) =
    this@sampleInIntervals
        .groupBy { calculateIntervalIndex(it, intervalSize) }
        .map { it.getLastMeasurementInGroup(intervalSize) }
        .sortedBy { it.measurementTime }

private fun calculateIntervalIndex(measurement: Measurement, intervalSize: Int): LocalDateTime {
    val adjustedTime = adjustToIntervalEnd(measurement.measurementTime, intervalSize)
    val totalMinutes = ChronoUnit.MINUTES.between(LocalDateTime.MIN, adjustedTime)
    val intervalIndexInMinutes = totalMinutes / intervalSize * intervalSize

    return LocalDateTime.MIN.plusMinutes(intervalIndexInMinutes)
}

private fun adjustToIntervalEnd(time: LocalDateTime, intervalSize: Int): LocalDateTime {
    return if (time.minute % intervalSize == 0 && time.second == 0) {
        time.minusMinutes(1)
    } else time
}

private fun Map.Entry<LocalDateTime, List<Measurement>>.getLastMeasurementInGroup(intervalSize: Int): Measurement =
    value.maxByOrNull { it.measurementTime }!!
        .copy(measurementTime = key.plusMinutes(intervalSize * 1L))
