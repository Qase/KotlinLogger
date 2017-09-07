package quanti.com.kotlinlog.utils

import java.util.*

/**
 * Created by Trnka Vladislav on 07.09.2017.
 *
 * Mockable class for time shifting during testing
 */


object ActualTime {

    val ONE_DAY_IN_MILLIS = 86400000L
    private var setTime = 0L

    fun currentDate() = Date(currentTimeMillis())

    fun shiftByOneDay() = shift(ONE_DAY_IN_MILLIS)
    fun shiftByOneDayBackward() = shift(-ONE_DAY_IN_MILLIS)

    fun currentTimeMillis(): Long {
        return System.currentTimeMillis() + setTime
    }


    /**
     * Moves current time by offset
     * Forward or backward
     */
    fun shift(timeShift: Long){
        setTime += timeShift
    }
}