package quanti.com.kotlinlog.file

import quanti.com.kotlinlog.base.LogLevel


/**
 * Created by Trnka Vladislav on 27.06.2017.
 *
 * FileLoggerBundle specifies settings for fiel logger
 *
 * @param maxDaysSaved max days of logging - default 7
 * @param minimalLogLevel minimalLogLevel - default LogLevel.Verbose
 *
 */

data class FileLoggerBundle @JvmOverloads constructor(
        var minimalLogLevel: Int = LogLevel.VERBOSE,
        val maxDaysSaved: Int = 7,
        val maxLogsPerOneFile: Int = 20,
        val sdCardFolderName: String = "KotlinLogger"
)

