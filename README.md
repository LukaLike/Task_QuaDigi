A program that is able to sample time-based measurement data received from a medical device. The measurement data have the following structure:

```kotlin
class Measurement
{
    private DateTime measurementTime;
    private Double measurementValue;
    private MeasurementType type;
}
```

Possible types of measurements are temperature, heart rate or SpO2. Measurements are measured exact to the second.

This program samples the received measurements into a 5-minute interval based on the following rules:
- each type of measurement shall be sampled separately
- from a 5-minute interval only the last measurement shall be taken
- if a measurement timestamp will exactly match a 5-minute interval border, it shall be used
for the current interval
- the input values are not sorted by time
- the output shall be sorted by time ascending

Example:
```
INPUT:
{2017-01-03T10:04:45, TEMP, 35.79}
{2017-01-03T10:01:18, SPO2, 98.78}
{2017-01-03T10:09:07, TEMP, 35.01}
{2017-01-03T10:03:34, SPO2, 96.49}
{2017-01-03T10:02:01, TEMP, 35.82}
{2017-01-03T10:05:00, SPO2, 97.17}
{2017-01-03T10:05:01, SPO2, 95.08}
OUTPUT:
{2017-01-03T10:05:00, TEMP, 35.79}
{2017-01-03T10:10:00, TEMP, 35.01}
{2017-01-03T10:05:00, SPO2, 97.17}
{2017-01-03T10:10:00, SPO2, 95.08}
```
