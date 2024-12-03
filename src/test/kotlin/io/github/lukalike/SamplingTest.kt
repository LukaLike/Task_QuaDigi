package io.github.lukalike

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.test.assertEquals

/**
 * Each `expectedOutput`'s value corresponds to the interval value its key belongs to (expected output)
 */
class SamplingTest {

    @Test
    fun `correctly samples single type & single interval`() {
        val expectedOutput = mapOf(
            defaultMeasurement(defaultDate(4, 15)) to defaultDate(5, 0)
        )

        val inputMeasurements = listOf(
            defaultMeasurement(defaultDate(1, 25)),
            defaultMeasurement(defaultDate(3, 59)),
            defaultMeasurement(defaultDate(4, 14))
        ) + expectedOutput.keys

        assertEquals(expectedOutput.convertValuesToIntervalBorders(), inputMeasurements.sample())
    }

    @Test
    fun `correctly samples single type & multiple intervals`() {
        val expectedOutput = mapOf(
            defaultMeasurement(defaultDate(4, 15)) to defaultDate(5, 0),
            defaultMeasurement(defaultDate(25, 0)) to defaultDate(25, 0),
            defaultMeasurement(defaultDate(34, 59)) to defaultDate(35, 0),
        )

        val inputMeasurements = listOf(
            defaultMeasurement(defaultDate(1, 25)),
            defaultMeasurement(defaultDate(4, 14)),
            defaultMeasurement(defaultDate(20, 1)),
            defaultMeasurement(defaultDate(24, 59)),
            defaultMeasurement(defaultDate(33, 33)),
            defaultMeasurement(defaultDate(34, 0))
        ) + expectedOutput.keys

        assertEquals(expectedOutput.convertValuesToIntervalBorders(), inputMeasurements.sample())
    }

    @Test
    fun `correctly samples single type inputs at border`() {
        val expectedOutput = mapOf(
            defaultMeasurement(defaultDate(5, 0)) to defaultDate(5, 0),
            defaultMeasurement(defaultDate(10, 0)) to defaultDate(10, 0),
            defaultMeasurement(defaultDate(10, 1)) to defaultDate(15, 0)
        )

        val inputMeasurements = listOf(
            defaultMeasurement(defaultDate(1, 15)),
            defaultMeasurement(defaultDate(6, 15)),
        ) + expectedOutput.keys

        assertEquals(expectedOutput.convertValuesToIntervalBorders(), inputMeasurements.sample())
    }

    @Test
    fun `correctly samples single type inputs around midnight`() {
        val expectedOutput = mapOf(
            defaultMeasurement(
                LocalDateTime.of(2024, 11, 27, 0, 0, 0)
            ) to LocalDateTime.of(2024, 11, 27, 0, 0, 0),
            defaultMeasurement(
                LocalDateTime.of(2024, 11, 28, 0, 0, 0)
            ) to LocalDateTime.of(2024, 11, 28, 0, 0, 0),
            defaultMeasurement(
                LocalDateTime.of(2024, 11, 28, 0, 5, 0)
            ) to LocalDateTime.of(2024, 11, 28, 0, 5, 0)
        )

        val inputMeasurements = listOf(
            defaultMeasurement(LocalDateTime.of(2024, 11, 26, 23, 55, 1)),
            defaultMeasurement(LocalDateTime.of(2024, 11, 26, 23, 59, 59)),
            defaultMeasurement(LocalDateTime.of(2024, 11, 27, 23, 56, 15)),
            defaultMeasurement(LocalDateTime.of(2024, 11, 27, 23, 58, 59)),
            defaultMeasurement(LocalDateTime.of(2024, 11, 28, 0, 0, 1)),
            defaultMeasurement(LocalDateTime.of(2024, 11, 28, 0, 0, 2)),
        ) + expectedOutput.keys

        assertEquals(expectedOutput.convertValuesToIntervalBorders(), inputMeasurements.sample())
    }

    @Test
    fun `correctly samples single type only at borders`() {
        val expectedOutput = mapOf(
            defaultMeasurement(defaultDate(5, 0)) to defaultDate(5, 0),
            defaultMeasurement(defaultDate(10, 0)) to defaultDate(10, 0),
            defaultMeasurement(defaultDate(20, 0)) to defaultDate(20, 0)
        )

        assertEquals(expectedOutput.convertValuesToIntervalBorders(), expectedOutput.keys.toList().sample())
    }

    @Test
    fun `correctly samples single type at big date difference`() {
        val expectedOutput = mapOf(
            defaultMeasurement(
                LocalDateTime.of(2024, 1, 1, 0, 0, 0)
            ) to LocalDateTime.of(2024, 1, 1, 0, 0, 0),
            defaultMeasurement(
                LocalDateTime.of(2024, 2, 10, 7, 59, 59)
            ) to LocalDateTime.of(2024, 2, 10, 8, 0, 0),
            defaultMeasurement(
                LocalDateTime.of(2024, 12, 31, 7, 0, 1)
            ) to LocalDateTime.of(2024, 12, 31, 7, 5, 0),
            defaultMeasurement(
                LocalDateTime.of(2025, 6, 30, 8, 0, 0)
            ) to LocalDateTime.of(2025, 6, 30, 8, 0, 0)
        )

        val inputMeasurements = listOf(
            defaultMeasurement(LocalDateTime.of(2023, 12, 31, 23, 59, 59)),
            defaultMeasurement(LocalDateTime.of(2024, 2, 10, 7, 59, 58)),
            defaultMeasurement(LocalDateTime.of(2025, 6, 30, 7, 59, 1)),
        ) + expectedOutput.keys

        assertEquals(expectedOutput.convertValuesToIntervalBorders(), inputMeasurements.sample())
    }


    @Test
    fun `correctly samples and orders single type & multiple intervals`() {
        val expectedOutput = mapOf(
            defaultMeasurement(defaultDate(4, 59)) to defaultDate(5, 0),
            defaultMeasurement(defaultDate(28, 18)) to defaultDate(30, 0),
            defaultMeasurement(defaultDate(50, 0)) to defaultDate(50, 0)
        )

        val inputMeasurements = listOf(
            defaultMeasurement(defaultDate(25, 15)),
            defaultMeasurement(defaultDate(1, 42)),
            defaultMeasurement(defaultDate(45, 1))
        ) + expectedOutput.keys

        assertEquals(expectedOutput.convertValuesToIntervalBorders(), inputMeasurements.sample())
    }

    @Test
    fun `correctly samples multiple types & multiple intervals`() {
        val expectedOutput = mapOf(
            defaultMeasurement(
                defaultDate(15, 0), MeasurementType.TEMP
            ) to defaultDate(15, 0),
            defaultMeasurement(
                defaultDate(35, 0), MeasurementType.TEMP
            ) to defaultDate(35, 0),
            defaultMeasurement(
                defaultDate(13, 14), MeasurementType.SPO2
            ) to defaultDate(15, 0),
            defaultMeasurement(
                defaultDate(35, 0), MeasurementType.SPO2
            ) to defaultDate(35, 0)
        )

        val inputMeasurements = listOf(
            defaultMeasurement(defaultDate(12, 1), MeasurementType.TEMP),
            defaultMeasurement(defaultDate(31, 15), MeasurementType.SPO2),
            defaultMeasurement(defaultDate(12, 16), MeasurementType.SPO2),
            defaultMeasurement(defaultDate(32, 14), MeasurementType.TEMP)
        ) + expectedOutput.keys

        assertEquals(expectedOutput.convertValuesToIntervalBorders(), inputMeasurements.sample())
    }

    @Test
    fun `correctly samples multiple types & multiple intervals when duplicated dates exist`() {
        val expectedOutput = mapOf(
            defaultMeasurement(defaultDate(15, 0), MeasurementType.TEMP) to defaultDate(15, 0),
            defaultMeasurement(defaultDate(35, 0), MeasurementType.TEMP) to defaultDate(35, 0),
            defaultMeasurement(defaultDate(13, 14), MeasurementType.SPO2) to defaultDate(15, 0),
            defaultMeasurement(defaultDate(35, 0), MeasurementType.SPO2) to defaultDate(35, 0)
        )

        val inputMeasurements = listOf(
            defaultMeasurement(defaultDate(12, 1), MeasurementType.TEMP),
            defaultMeasurement(defaultDate(31, 15), MeasurementType.SPO2),
            defaultMeasurement(defaultDate(12, 16), MeasurementType.SPO2),
            defaultMeasurement(defaultDate(32, 14), MeasurementType.TEMP)
        ) + expectedOutput.keys

        assertEquals(expectedOutput.convertValuesToIntervalBorders(), inputMeasurements.sample())
    }

    @Test
    fun `correctly samples when providing different starting point`() {
        val expectedOutput = mapOf(
            defaultMeasurement(defaultDate(8, 15)) to defaultDate(10, 0),
            defaultMeasurement(defaultDate(15, 0)) to defaultDate(15, 0)
        )

        val inputMeasurements = listOf(
            defaultMeasurement(defaultDate(1, 25)),
            defaultMeasurement(defaultDate(7, 10)),
            defaultMeasurement(defaultDate(12, 15)),
        ) + expectedOutput.keys

        assertEquals(expectedOutput.convertValuesToIntervalBorders(), inputMeasurements.sample(defaultDate(6, 0)))
    }

    @Test
    fun `correctly samples empty list`() {
        assertEquals(listOf(), listOf<Measurement>().sample())
    }

    private fun Map<Measurement, LocalDateTime>.convertValuesToIntervalBorders() =
        mapKeys { it.key.copy(measurementTime = it.value) }.keys.toList()

    private fun defaultMeasurement(
        measurementTime: LocalDateTime,
        type: MeasurementType = MeasurementType.TEMP,
        measurementValue: Double = Random.nextDouble()
    ) =
        Measurement(measurementTime, type, measurementValue)

    private fun defaultDate(minute: Int, second: Int) =
        LocalDateTime.of(2024, 11, 27, 18, minute, second)

}
