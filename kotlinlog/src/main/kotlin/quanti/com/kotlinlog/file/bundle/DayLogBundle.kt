package quanti.com.kotlinlog.file.bundle

import quanti.com.kotlinlog.base.LogLevel


/**
 * Created by Trnka Vladislav on 27.06.2017.
 *
 * DayLogBundle specifies settings for file logger
 *
 * @param maxDaysSaved max days of logging - default 7
 * @param minimalLogLevel minimalLogLevel - default LogLevel.Verbose
 *
 */

data class DayLogBundle @JvmOverloads constructor(
        val minimalLogLevel: Int = LogLevel.VERBOSE,
        val maxDaysSaved: Int = 7
) : IBundle {

    override fun getMinimalLogLevelInt() = minimalLogLevel
}

