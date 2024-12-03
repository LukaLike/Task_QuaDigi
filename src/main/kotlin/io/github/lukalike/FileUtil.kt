package io.github.lukalike

import java.io.File
import java.io.FileNotFoundException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Utility methods to read/write measurement data from/to file
 */

@Suppress("SpellCheckingInspection")
const val MEASUREMENT_OUTPUT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
val outputFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(MEASUREMENT_OUTPUT_FORMAT)

fun readMeasurementData(): List<Measurement> {
    val inputStream = {}.javaClass.getResourceAsStream("/input.txt")
        ?: throw FileNotFoundException("/input.txt not found under resources folder!")

    return inputStream.bufferedReader().readLines().mapIndexedNotNull { index, line ->
        val inputFields = line
            .removeSurrounding("{", "}")
            .split(",")
            .map { it.trim() }

        try {
            Measurement(
                LocalDateTime.parse(inputFields[0]),
                MeasurementType.valueOf(inputFields[1]),
                inputFields[2].toDouble()
            )
        } catch (e: Exception) {
            println("Could not parse [$line] at line [$index]: $e")
            null
        }
    }
}

fun List<Measurement>.outputResults() {
    printToConsole()
    outputToResourcesFile()
}

fun List<Measurement>.printToConsole() {
    forEach { println(it.constructMeasurementOutput()) }
}

fun List<Measurement>.outputToResourcesFile() {
    val resourcesUrl = {}.javaClass.getResource("/")
        ?: throw IllegalArgumentException("Resources folder not found!")

    val resourcesPath = File(resourcesUrl.toURI())
    val outputFile = File(resourcesPath, "output.txt")

    outputFile.createNewFile()
    outputFile.writeText("")

    outputFile.bufferedWriter().use { writer ->
        forEach {
            writer.write(it.constructMeasurementOutput())
            writer.newLine()
        }
    }
}

private fun Measurement.constructMeasurementOutput() =
    "{${outputFormatter.format(measurementTime)}, $type, $measurementValue}"
