package quanti.com.kotlinlog.file.bundle

import quanti.com.kotlinlog.base.LogLevel


/**
 * Created by Trnka Vladislav on 27.06.2017.
 *
 * DayLogBundle specifies settings for file logger
 *
 * @param minimalLogLevel minimalLogLevel - default LogLevel.Verbose
 * @param fileSize max size in MB of one file - default 5 MB
 * @param numOfFiles how many files each of 'fileSize' size to keep - default 10
 */
data class CircleLogBundle @JvmOverloads constructor(
        val minimalLogLevel: Int = LogLevel.VERBOSE,
        val fileSize: Int = 5,
        val numOfFiles: Int = 10
)