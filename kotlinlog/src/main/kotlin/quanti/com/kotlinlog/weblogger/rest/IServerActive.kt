package quanti.com.kotlinlog.weblogger.rest

/**
 * Callback interface to get first connection state of server
 */
interface IServerActive {

    fun isServerActive(isActive: Boolean)
}