package quanti.com.kotlinlog.weblogger.bundle

import quanti.com.kotlinlog.base.LogLevel
import quanti.com.kotlinlog.utils.getRandomString
import quanti.com.kotlinlog.weblogger.api.IApiServerActive

/**
 * Bundle class for quanti web api logging server
 *
 * @param url okhttp url address of the server - it has to start with 'http://' and end with '/'
 * @param severActive callback to check if server is active
 * @param minimalLogLevel minimalLogLevel - default LogLevel.Verbose
 * @param minimalLogLevelThrowable minimal log level for calls with throwable - default LogLevel.Verbose
 * @param sessionName session name identificator on webserver
 */
data class WebServerApiBundle @JvmOverloads constructor(
        val url: String,
        val severActive: IApiServerActive = dummyIsServerActiveImplementation,
        val minimalLogLevel: Int = LogLevel.DEBUG,
        val minimalLogLevelThrowable: Int = LogLevel.VERBOSE,
        val sessionName: String = "KotlinLoggerSession_" + getRandomString(4)
) {

    companion object {
        val dummyIsServerActiveImplementation = object : IApiServerActive {
            override fun isServerActive(isActive: Boolean) {}
        }
    }
}

