package quanti.com.kotlinlog.base

/**
 * Created by Trnka Vladislav on 30.05.2017.
 *
 * Logger interface for derived logger
 */
interface ILogger {

    /**
     * Basic log
     */
    fun log(androidLogLevel: Int, tag: String, methodName: String, text: String)

    /**
     * Log error
     */
    fun logThrowable(tag: String, methodName: String, text: String, t: Throwable)

    /**
     * Force log to be written synchronously
     */
    fun logSync(androidLogLevel: Int, tag: String, methodName: String, text: String)

}
