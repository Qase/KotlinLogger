package quanti.com.kotlinlog.weblogger.websocket

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import quanti.com.kotlinlog.utils.loga
import quanti.com.kotlinlog.weblogger.IWebLoggerEntitySender
import quanti.com.kotlinlog.weblogger.IWebLoggerEntitySender.Companion.client
import quanti.com.kotlinlog.weblogger.IWebLoggerEntitySender.Companion.gson
import quanti.com.kotlinlog.weblogger.entity.WebLoggerEntity
import quanti.com.kotlinlog.weblogger.rest.IServerActive

class WebSocketSender(url: String)
    : WebSocketListener(), IWebLoggerEntitySender {


    private val ws: WebSocket
    private var connected : Boolean? = null
    private var callback : IServerActive? = null




    init {
        val request = Request.Builder().url(url).build()
        ws = client.newWebSocket(request, this)
    }

    override fun send(entity: WebLoggerEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            val serializedEntity = gson.toJson(arrayListOf(entity))
            ws.send(serializedEntity)
        }
    }

    override fun sendSync(entity: WebLoggerEntity) = send(entity)

    override fun checkConnection(callback: IServerActive, checkConnectionEntity: ArrayList<WebLoggerEntity>) {
        if (connected != null){
            callback.isServerActive(connected!!)
        } else {
            this.callback = callback
        }
    }

    override fun clean() {
        ws.close(NORMAL_CLOSURE_STATUS, "Client requested to end stream.")
        connected = false
    }

    override fun onOpen(webSocket: WebSocket?, response: Response) {
        connected = true
        loga(TAG, "WS open :", response.message())

        if (callback != null){
            callback?.isServerActive(true)
            callback = null
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        loga(TAG, "Receiving :", text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        loga(TAG, "Receiving bytes : " + bytes.hex())
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        loga(TAG, "Closing : $code / $reason")
        connected = false
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable, response: Response?) {
        connected = false
        loga(TAG, "Error : " + t.message)

        if (callback != null){
            callback?.isServerActive(false)
            callback = null
        }
    }

    companion object {
        private const val TAG = "WebSocketSender"
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
}