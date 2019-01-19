package quanti.com.kotlinlog.weblogger.api

import quanti.com.kotlinlog.weblogger.entity.WebLoggerEntity
import retrofit2.http.Body
import retrofit2.http.POST


interface WebServerApi {

    @POST("log")
    fun postLogs(@Body logs: List<WebLoggerEntity>)

}

