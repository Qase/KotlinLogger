package quanti.com.kotlinlog.weblogger

import quanti.com.kotlinlog.base.ILogger
import quanti.com.kotlinlog.utils.convertToLogCatString
import quanti.com.kotlinlog.weblogger.bundle.WebLoggerBundle
import quanti.com.kotlinlog.weblogger.entity.WebLoggerEntity
import quanti.com.kotlinlog.weblogger.websocket.WebSocketSender

class WebSocketLogger(
        private val bun : WebLoggerBundle
) : ILogger {

    private val sender = WebSocketSender(bun.url)


    override fun log(androidLogLevel: Int, tag: String, methodName: String, text: String) {
        if (androidLogLevel < bun.minimalLogLevel)
            return

        val message = "$tag/$methodName: $text"
        val entity = WebLoggerEntity(bun.sessionName, androidLogLevel, message)
        sender.send(entity)
    }

    override fun logThrowable(androidLogLevel: Int, tag: String, methodName: String, text: String, t: Throwable) {
        if (androidLogLevel < bun.minimalLogLevelThrowable)
            return

        var message = "$tag/$methodName: $text/n"
        message += t.convertToLogCatString()

        val entity = WebLoggerEntity(bun.sessionName, androidLogLevel, message)
        sender.send(entity)
    }

    override fun logSync(androidLogLevel: Int, tag: String, methodName: String, text: String) = log(androidLogLevel, tag, methodName, text)

    override fun cleanResources() {
        sender.clean()
    }
}