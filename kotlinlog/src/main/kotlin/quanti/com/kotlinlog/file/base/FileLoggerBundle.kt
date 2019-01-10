package quanti.com.kotlinlog.file.base

import quanti.com.kotlinlog.base.LogLevel


/**
 * Created by Trnka Vladislav on 27.06.2017.
 *
 * FileLoggerBundle specifies settings for file logger
 *
 * @param maxDaysSaved max days of logging - default 7
 * @param minimalLogLevel minimalLogLevel - default LogLevel.Verbose
 *
 */

data class FileLoggerBundle @JvmOverloads constructor(
        val minimalLogLevel: Int = LogLevel.VERBOSE,
        val maxDaysSaved: Int = 1
//todo add which loger to use enum
)

