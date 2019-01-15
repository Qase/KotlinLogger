package quanti.com.kotlinlog.file.bundle

import quanti.com.kotlinlog.base.LogLevel


/**
 * Created by Trnka Vladislav on 27.06.2017.
 *
 * DayLogBundle specifies settings for file logger
 *
 * @param maxDaysSaved max days of keeping logs- default 7
 * @param minimalLogLevel minimalLogLevel - default LogLevel.Verbose
 * @param maxDaysSavedThrowable max days of keeping exception logs
 * @param minimalOwnFileLogLevelThrowable minimal log level when create separate file for exception - default LogLevel.Verbose
 */

class DayLogBundle @JvmOverloads constructor(
        minimalLogLevel: Int = LogLevel.VERBOSE,
        val maxDaysSaved: Int = 7,
        maxDaysSavedThrowable: Int = 10,
        minimalOwnFileLogLevelThrowable: Int = LogLevel.VERBOSE
) : BaseBundle(minimalLogLevel, maxDaysSavedThrowable, minimalOwnFileLogLevelThrowable)
