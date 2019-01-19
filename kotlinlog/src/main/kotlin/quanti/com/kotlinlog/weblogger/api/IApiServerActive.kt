package quanti.com.kotlinlog.weblogger.api

/**
 * Callback interface to get first connection state of server
 */
interface IApiServerActive {

    fun isServerActive(isActive: Boolean)
}