package quanti.com.kotlinlog.weblogger

import quanti.com.kotlinlog.base.ILogger
import quanti.com.kotlinlog.utils.convertToLogCatString
import quanti.com.kotlinlog.weblogger.bundle.BaseWebLoggerBundle
import quanti.com.kotlinlog.weblogger.bundle.RestLoggerBundle
import quanti.com.kotlinlog.weblogger.bundle.WebSocketLoggerBundle
import quanti.com.kotlinlog.weblogger.entity.WebLoggerEntity
import quanti.com.kotlinlog.weblogger.rest.RestSender
import quanti.com.kotlinlog.weblogger.websocket.WebSocketSender

/**
 * WebLogger to quanti log webserver
 *
 * You can use either WebSocket protocol or classic REST api
 *
 * WebSocket is suppose to be faster and instant
 * REST api goes in batches of 25 messages
 *
 *
 *
 */
class WebLogger(private val bun: BaseWebLoggerBundle) : ILogger {


    private val sender = when (bun) {
        is RestLoggerBundle -> RestSender(bun.url)
        is WebSocketLoggerBundle -> WebSocketSender(bun.url)
        else -> {
            throw Exception("Unknown file bundle.")
        }
    }

    init {
        sender.checkConnection(bun.severActive)
    }

    override fun log(androidLogLevel: Int, tag: String, methodName: String, text: String) {
        val message = "$tag/$methodName: $text"
        val entity = WebLoggerEntity(bun.sessionName, androidLogLevel, message)

        sender.send(entity)
    }

    override fun logThrowable(androidLogLevel: Int, tag: String, methodName: String, text: String, t: Throwable) {
        var message = "$tag/$methodName: $text/n"
        message += t.convertToLogCatString()

        val entity = WebLoggerEntity(bun.sessionName, androidLogLevel, message)
        sender.send(entity)
    }

    override fun logSync(androidLogLevel: Int, tag: String, methodName: String, text: String) {
        val message = "$tag/$methodName: $text"
        val entity = WebLoggerEntity(bun.sessionName, androidLogLevel, message)

        sender.send(entity)
    }

    override fun cleanResources() {
        sender.clean()
    }

    override fun getMinimalLoggingLevel(): Int = bun.minimalLogLevel

    override fun describe(): String {
        return "WebLogger_${sender.javaClass.simpleName}"
    }


}