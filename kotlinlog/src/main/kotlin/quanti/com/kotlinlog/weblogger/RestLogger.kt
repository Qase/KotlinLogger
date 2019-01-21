package quanti.com.kotlinlog.weblogger

import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import quanti.com.kotlinlog.base.ILogger
import quanti.com.kotlinlog.utils.convertToLogCatString
import quanti.com.kotlinlog.utils.loga
import quanti.com.kotlinlog.weblogger.bundle.WebLoggerBundle
import quanti.com.kotlinlog.weblogger.entity.WebLoggerEntity
import quanti.com.kotlinlog.weblogger.rest.RestApiDefinition
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * RestLogger to quanti log webserver
 *
 *
 *
 */
class RestLogger(private val bun: WebLoggerBundle) : ILogger {

    private val loggerApi: RestApiDefinition
    private val threadExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val blockingQueue = LinkedBlockingQueue<WebLoggerEntity>()


    init {

        if (!bun.url.endsWith("/api/v1/")) {
            Log.e("RestLogger", "Url doesn't end with /api/v1/. Have you specified correct webserver endpoint?")
        }


        Log.d("RestLogger", "Creating connection to ${bun.url}")
        val retrofit = Retrofit.Builder()
                .baseUrl(bun.url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        loggerApi = retrofit.create<RestApiDefinition>(RestApiDefinition::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            val list = arrayListOf(WebLoggerEntity.getTestEntity())
            val response = loggerApi.postLogs(list).execute()

            withContext(Dispatchers.Main) {
                bun.severActive.isServerActive(response.isSuccessful)
            }
        }

        val func = Runnable {
            loga("RestLogger", "TASK: Writing data from queue: ${blockingQueue.size}")

            if (blockingQueue.isEmpty())
                return@Runnable

            val listOfLogs = blockingQueue.toList()
            blockingQueue.clear()

            listOfLogs
                    .chunked(25)
                    .forEach(this@RestLogger::post)
        }

        threadExecutor.scheduleAtFixedRate(func, 1, 5, TimeUnit.SECONDS)

    }

    override fun log(androidLogLevel: Int, tag: String, methodName: String, text: String) {
        if (androidLogLevel < bun.minimalLogLevel)
            return

        val message = "$tag/$methodName: $text"
        val entity = WebLoggerEntity(bun.sessionName, androidLogLevel, message)

        blockingQueue.add(entity)
    }

    override fun logThrowable(androidLogLevel: Int, tag: String, methodName: String, text: String, t: Throwable) {

        if (androidLogLevel < bun.minimalLogLevelThrowable)
            return

        var message = "$tag/$methodName: $text/n"
        message += t.convertToLogCatString()

        val entity = WebLoggerEntity(bun.sessionName, androidLogLevel, message)

        post(entity)
    }

    override fun logSync(androidLogLevel: Int, tag: String, methodName: String, text: String) {

        if (androidLogLevel < bun.minimalLogLevel)
            return

        val message = "$tag/$methodName: $text"
        val entity = WebLoggerEntity(bun.sessionName, androidLogLevel, message)

        post(entity)
    }

    override fun cleanResources() {
        threadExecutor.shutdown()
    }

    private fun post(entity: WebLoggerEntity) = post(arrayListOf(entity))

    private fun post(list: List<WebLoggerEntity>) {
        GlobalScope.launch(Dispatchers.IO) {
            loga("RestLogger", "TASK: Writing data of size: ${list.size}")
            loggerApi.postLogs(list).execute()
        }
    }


    companion object {
        val gson = GsonBuilder().create()!!
    }


}