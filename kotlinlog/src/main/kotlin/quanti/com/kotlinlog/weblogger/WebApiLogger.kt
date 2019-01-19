package quanti.com.kotlinlog.weblogger

import android.util.Log
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import quanti.com.kotlinlog.base.ILogger
import quanti.com.kotlinlog.base.getWebLoggerString
import quanti.com.kotlinlog.utils.convertToLogCatString
import quanti.com.kotlinlog.utils.getRandomString
import quanti.com.kotlinlog.utils.loga
import quanti.com.kotlinlog.weblogger.api.WebServerApi
import quanti.com.kotlinlog.weblogger.entity.WebLoggerEntity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * WebApiLogger to quanti log webserver
 *
 *
 *
 */
class WebApiLogger(url: String,
                   private val sessionName: String = "KotlinLoggerSession" + getRandomString(4)

) : ILogger {


    private val loggerApi: WebServerApi
    private val threadExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val blockingQueue = LinkedBlockingQueue<WebLoggerEntity>()


    init {
        checkUrl(url)

        Log.d("TAG", "Creating connection to $url")
        val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        loggerApi = retrofit.create<WebServerApi>(WebServerApi::class.java)

        val func = Runnable {
            loga("TASK: Writing data from queue: ${blockingQueue.size}")

            val listOfLogs = blockingQueue.toList()
            blockingQueue.clear()

            loggerApi.postLogs(listOfLogs)
        }

        threadExecutor.scheduleAtFixedRate(func, 1, 5, TimeUnit.SECONDS)


    }

    private fun checkUrl(url: String) {
        //baseUrl must end in /
        if (!url.startsWith("http://")) {
            Log.d("WebApiLogger", "Url doesn't start with http://")
            //todo is that necessary??
        }

        if (!url.endsWith("/api/v1/")) {
            Log.d("WebApiLogger", "Url doesn't end with /api/v1/")
            //todo is that necessary??
        }

        if (!url.endsWith("/")) {
            Log.d("WebApiLogger", "Url doesn't end with /")
        }
    }

    override fun log(androidLogLevel: Int, tag: String, methodName: String, text: String) {
        val entity = WebLoggerEntity(
                id = "",
                sessionName = sessionName,
                timestamp = Date().time,
                severity = androidLogLevel.getWebLoggerString(),
                message = "$tag/$methodName: $text"
        )

        blockingQueue.add(entity)
    }

    override fun logThrowable(androidLogLevel: Int, tag: String, methodName: String, text: String, t: Throwable) {

        var message = "$tag/$methodName: $text/n"
        message += t.convertToLogCatString()

        val entity = WebLoggerEntity(
                id = "",
                sessionName = sessionName,
                timestamp = Date().time,
                severity = androidLogLevel.getWebLoggerString(),
                message = message
        )

        //we want errors to be sync
        GlobalScope.launch(Dispatchers.IO) {
            loggerApi.postLogs(listOf(entity))
        }
    }

    override fun logSync(androidLogLevel: Int, tag: String, methodName: String, text: String) {
        val entity = WebLoggerEntity(
                id = "",
                sessionName = sessionName,
                timestamp = Date().time,
                severity = androidLogLevel.getWebLoggerString(),
                message = "$tag/$methodName: $text"
        )


        GlobalScope.launch(Dispatchers.IO) {
            loggerApi.postLogs(listOf(entity))
        }
    }

    override fun cleanResources() {
        threadExecutor.shutdown()
    }


    companion object {
        val gson = GsonBuilder().create()!!
    }


}