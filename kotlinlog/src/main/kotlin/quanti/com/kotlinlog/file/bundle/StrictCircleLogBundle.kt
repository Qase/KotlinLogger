package quanti.com.kotlinlog.file.bundle

import quanti.com.kotlinlog.base.LogLevel


/**
 * Created by Trnka Vladislav on 27.06.2017.
 *
 * StrictCircleLogBundle specifies settings for file logger
 *
 * @param minimalLogLevel minimalLogLevel - default LogLevel.Verbose
 * @param maxFileSize max size in bytes of one file - default 5242880 B -> 5 MB
 * @param numOfFiles how many files each of 'fileSize' size to keep - default 10
 * @param maxDaysSavedThrowable max days of keeping exception logs
 * @param minimalOwnFileLogLevelThrowable minimal log level when create separate file for exception - default LogLevel.Verbose
 */

class StrictCircleLogBundle @JvmOverloads constructor(
        minimalLogLevel: Int = LogLevel.VERBOSE,
        val maxFileSize: Long = 5242880L,
        val numOfFiles: Int = 10,
        maxDaysSavedThrowable: Int = 10,
        minimalOwnFileLogLevelThrowable: Int = LogLevel.VERBOSE
) : BaseBundle(minimalLogLevel, maxDaysSavedThrowable, minimalOwnFileLogLevelThrowable) {


    constructor(minimalLogLevel: Int = LogLevel.VERBOSE,
                maxFileSizeMegaBytes: Int,
                numOfFiles: Int = 10,
                maxDaysSavedThrowable: Int = 10,
                minimalOwnFileLogLevelThrowable: Int = LogLevel.VERBOSE
    ) :
            this(minimalLogLevel, maxFileSizeMegaBytes * ONE_MEGABYTE,
                    numOfFiles, maxDaysSavedThrowable, minimalOwnFileLogLevelThrowable)

    companion object {
        const val ONE_MEGABYTE = 1048576L
    }

}