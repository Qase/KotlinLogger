package quanti.com.kotlinlog.weblogger.bundle

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
abstract class BaseWebLoggerBundle constructor(
        val url: String,
        val severActive: IServerActive,
        val minimalLogLevel: Int,
        val minimalLogLevelThrowable : Int,
        val sessionName : String
) {

    companion object {
        val dummyIsServerActiveImplementation = object : IServerActive {
            override fun isServerActive(isActive: Boolean) {}
        }
    }
}

