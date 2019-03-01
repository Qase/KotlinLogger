package quanti.com.kotlinlog.weblogger

import com.google.gson.Gson
import okhttp3.OkHttpClient
import quanti.com.kotlinlog.weblogger.entity.WebLoggerEntity
import quanti.com.kotlinlog.weblogger.rest.IServerActive

interface IWebLoggerEntitySender {

    fun send(entity: WebLoggerEntity)

    fun sendSync(entity: WebLoggerEntity)

    fun clean()

    fun checkConnection(callback: IServerActive, checkConnectionEntity: ArrayList<WebLoggerEntity>)

    companion object {
        //OkHttpClients should be shared
        val client = OkHttpClient()
        val gson = Gson()
    }
}