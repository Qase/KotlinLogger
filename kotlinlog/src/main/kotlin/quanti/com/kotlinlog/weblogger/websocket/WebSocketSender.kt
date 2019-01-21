package quanti.com.kotlinlog.weblogger.websocket

import android.util.Log
import com.google.gson.Gson
import okhttp3.*
import okio.ByteString
import quanti.com.kotlinlog.utils.loga
import quanti.com.kotlinlog.weblogger.entity.WebLoggerEntity

class WebSocketSender(url: String)
    : WebSocketListener()
{

    private val ws: WebSocket

    init {
        val request = Request.Builder().url(url).build()
        ws = client.newWebSocket(request, this)
    }

    fun send(entity: WebLoggerEntity) = send(arrayListOf(entity))

    fun send(entities: List<WebLoggerEntity>){
        val serializedEntity = gson.toJson(entities)
        ws.send(serializedEntity)
    }

    fun clean(){
        ws.close(NORMAL_CLOSURE_STATUS, "Client requested to end stream.")
    }

    override fun onOpen(webSocket: WebSocket?, response: Response) {
        loga(TAG, "WS open :", response.message())
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        loga(TAG, "Recieving :", text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        loga(TAG,"Receiving bytes : " + bytes.hex())
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String?) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        Log.d(TAG,"Closing : $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable, response: Response?) {
        Log.d(TAG,"Error : " + t.message)
    }

    companion object {
        private const val TAG = "WebSocketSender"
        private const val NORMAL_CLOSURE_STATUS = 1000
        //OkHttpClients should be shared
        private val client =  OkHttpClient()
        private val gson = Gson()

    }
}