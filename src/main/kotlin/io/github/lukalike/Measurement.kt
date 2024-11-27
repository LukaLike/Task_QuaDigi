package io.github.lukalike

import java.time.LocalDateTime

data class Measurement(
    val measurementTime: LocalDateTime,
    val type: String,
    val measurementValue: Double
)
