package quanti.com.kotlinlog.weblogger.rest

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import quanti.com.kotlinlog.utils.loga
import quanti.com.kotlinlog.weblogger.IWebLoggerEntitySender
import quanti.com.kotlinlog.weblogger.IWebLoggerEntitySender.Companion.gson
import quanti.com.kotlinlog.weblogger.entity.WebLoggerEntity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class RestSender(url: String) : IWebLoggerEntitySender {

    private val loggerApi: RestApiDefinition
    private val threadExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val blockingQueue = LinkedBlockingQueue<WebLoggerEntity>()

    private val sendFunc = Runnable {
        if (blockingQueue.isEmpty())
            return@Runnable

        val listOfLogs = blockingQueue.toList()
        blockingQueue.clear()

        listOfLogs.chunked(25).forEach(this@RestSender::post)
    }

    init {
        Log.d("WebLogger", "Creating connection to $url")
        loggerApi = createRetrofit(url).create<RestApiDefinition>(RestApiDefinition::class.java)
        threadExecutor.scheduleAtFixedRate(sendFunc, 1, 5, TimeUnit.SECONDS)
    }

    override fun send(entity: WebLoggerEntity) {
        blockingQueue.add(entity)
    }

    override fun sendSync(entity: WebLoggerEntity) {
        post(arrayListOf(entity))
    }

    override fun clean() {
        threadExecutor.shutdown()
    }

    private fun post(list: List<WebLoggerEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            loga("WebLogger", "TASK: Writing data of size: ${list.size}")
            try{
                loggerApi.postLogs(list).execute()
            } catch (e : Exception){
                loga(e.toString())
            }
        }
    }

    private fun createRetrofit(url:String): Retrofit {
        checkUrl(url)

        return Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    override fun checkConnection(callback: IServerActive, checkConnectionEntity: ArrayList<WebLoggerEntity>) {
        GlobalScope.launch(Dispatchers.IO) {

            var connected = false
            try {
                val response = loggerApi.postLogs(checkConnectionEntity).execute()
                connected = response.isSuccessful
            } catch (e: Exception){
                loga(e.toString())
            }
            withContext(Dispatchers.Main) {
                callback.isServerActive(connected)
            }
        }
    }

    private fun checkUrl(url:String){
        if (!url.endsWith("/api/v1/")) {
            Log.e("WebLogger", "Url doesn't end with /api/v1/. Have you specified correct webserver endpoint?")
        }
    }

}