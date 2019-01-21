package quanti.com.kotlinlog.weblogger.bundle

import quanti.com.kotlinlog.base.LogLevel
import quanti.com.kotlinlog.utils.getRandomString
import quanti.com.kotlinlog.weblogger.rest.IServerActive

/**
 * Bundle class for quanti web api logging server
 *
 * @param url okhttp url address of the server - it has to start with 'http://' and end with '/'
 * @param severActive callback to check if server is active
 * @param minimalLogLevel minimalLogLevel - default LogLevel.Verbose
 * @param minimalLogLevelThrowable minimal log level for calls with throwable - default LogLevel.Verbose
 * @param sessionName session name identificator on webserver
 */
data class WebLoggerBundle @JvmOverloads constructor(
        val url: String,
        val severActive: IServerActive = dummyIsServerActiveImplementation,
        val minimalLogLevel: Int = LogLevel.DEBUG,
        val minimalLogLevelThrowable: Int = LogLevel.VERBOSE,
        val sessionName: String = "KotlinLoggerSession_" + getRandomString(4)
) {

    companion object {
        val dummyIsServerActiveImplementation = object : IServerActive {
            override fun isServerActive(isActive: Boolean) {}
        }
    }
}

