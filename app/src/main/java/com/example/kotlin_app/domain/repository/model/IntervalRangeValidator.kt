package com.example.kotlin_app.domain.repository.model


object IntervalRangeValidator {

    private val validIntervalForRange: Map<Range, Interval> = mapOf(
        Range.ONE_DAY to Interval.SIXTY_MIN,
        Range.SIX_MONTHS to Interval.ONE_DAY,
        Range.ONE_YEAR to Interval.ONE_DAY,
        Range.FIVE_YEARS to Interval.ONE_DAY,
    )

    fun getValidIntervalsFor(range: Range): Interval {
        return  validIntervalForRange[range]?: Interval.ONE_DAY
    }

    val allRanges = Range.entries
}