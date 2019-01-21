package quanti.com.kotlinlog.weblogger.rest

import quanti.com.kotlinlog.weblogger.entity.WebLoggerEntity
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface RestApiDefinition {

    @POST("log")
    fun postLogs(@Body logs: List<WebLoggerEntity>) : Call<Void>

}

