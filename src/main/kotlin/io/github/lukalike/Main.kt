package io.github.lukalike

import io.github.lukalike.util.outputResults
import io.github.lukalike.util.readMeasurementData
import io.github.lukalike.util.sample
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val measurementData = readMeasurementData()
    val sampledMeasurementData = measurementData.sample()

    sampledMeasurementData.outputResults()
}
