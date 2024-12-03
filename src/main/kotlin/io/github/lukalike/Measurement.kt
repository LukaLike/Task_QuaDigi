package io.github.lukalike

import java.time.LocalDateTime

enum class MeasurementType {
    HEART_RATE,
    SPO2,
    TEMP
}

data class Measurement(
    val measurementTime: LocalDateTime,
    val type: MeasurementType,
    val measurementValue: Double
)
