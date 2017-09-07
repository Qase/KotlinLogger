package quanti.com.kotlinlog.base

/**
 * Created by Trnka Vladislav on 27.06.2017.
 *
 * LoggerBundle specifies basic settings
 *
 * @param minimalLogLevel minimalLogLevel - default LogLevel.Verbose
 */

data class LoggerBundle @JvmOverloads constructor(
        var minimalLogLevel: Int = LogLevel.VERBOSE
)