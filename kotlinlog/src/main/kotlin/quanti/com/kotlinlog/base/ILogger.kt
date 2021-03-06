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
    fun logThrowable(androidLogLevel: Int, tag: String, methodName: String, text: String, t: Throwable)

    /**
     * Force log to be written synchronously
     */
    fun logSync(androidLogLevel: Int, tag: String, methodName: String, text: String)

    /**
     * Clean all allocated resources
     */
    fun cleanResources()

    /**
     * Return minimal log level form which to log
     */
    fun getMinimalLoggingLevel() : Int

    /**
     * Return textual description of this logger and associated bundle
     */
    fun describe(): String

}
