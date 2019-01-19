package quanti.com.kotlinlog.weblogger

import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import quanti.com.kotlinlog.base.ILogger
import quanti.com.kotlinlog.base.getWebLoggerString
import quanti.com.kotlinlog.utils.convertToLogCatString
import quanti.com.kotlinlog.utils.loga
import quanti.com.kotlinlog.weblogger.api.WebServerApi
import quanti.com.kotlinlog.weblogger.bundle.WebServerApiBundle
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
class WebApiLogger(private val bun: WebServerApiBundle) : ILogger {


    private val loggerApi: WebServerApi
    private val threadExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val blockingQueue = LinkedBlockingQueue<WebLoggerEntity>()


    init {

        if (!bun.url.endsWith("/api/v1/")) {
            Log.e("WebApiLogger", "Url doesn't end with /api/v1/. Have you specified correct webserver endpoint?")
        }


        Log.d("WebApiLogger", "Creating connection to ${bun.url}")
        val retrofit = Retrofit.Builder()
                .baseUrl(bun.url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        loggerApi = retrofit.create<WebServerApi>(WebServerApi::class.java)

        val func = Runnable {
            loga("WebApiLogger", "TASK: Writing data from queue: ${blockingQueue.size}")

            if (blockingQueue.isEmpty())
                return@Runnable

            val listOfLogs = blockingQueue.toList()
            blockingQueue.clear()

            listOfLogs
                    .chunked(25)
                    .forEach {
                        GlobalScope.launch(Dispatchers.IO) {
                            loga("WebApiLogger", "TASK: Writing chunked data of size: ${it.size}")
                            loggerApi.postLogs(it).execute()
                        }

                    }
        }

        threadExecutor.scheduleAtFixedRate(func, 1, 5, TimeUnit.SECONDS)


    }


    override fun log(androidLogLevel: Int, tag: String, methodName: String, text: String) {
        if (androidLogLevel < bun.minimalLogLevel)
            return

        val message = "$tag/$methodName: $text"
        val entity = WebLoggerEntity(bun.sessionName, androidLogLevel.getWebLoggerString(), message)

        blockingQueue.add(entity)
    }

    override fun logThrowable(androidLogLevel: Int, tag: String, methodName: String, text: String, t: Throwable) {

        if (androidLogLevel < bun.minimalLogLevelThrowable)
            return

        var message = "$tag/$methodName: $text/n"
        message += t.convertToLogCatString()

        val entity = WebLoggerEntity(bun.sessionName, androidLogLevel.getWebLoggerString(), message)

        //we want errors to be sync
        GlobalScope.launch(Dispatchers.IO) {
            loggerApi.postLogs(listOf(entity)).execute()
        }
    }

    override fun logSync(androidLogLevel: Int, tag: String, methodName: String, text: String) {

        if (androidLogLevel < bun.minimalLogLevel)
            return

        val message = "$tag/$methodName: $text"
        val entity = WebLoggerEntity(bun.sessionName, androidLogLevel.getWebLoggerString(), message)

        GlobalScope.launch(Dispatchers.IO) {
            loggerApi.postLogs(listOf(entity)).execute()
        }
    }

    override fun cleanResources() {
        threadExecutor.shutdown()
    }


    companion object {
        val gson = GsonBuilder().create()!!
    }


}