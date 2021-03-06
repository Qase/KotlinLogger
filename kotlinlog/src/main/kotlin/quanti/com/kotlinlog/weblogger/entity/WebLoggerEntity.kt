package quanti.com.kotlinlog.weblogger.entity

import com.google.gson.annotations.SerializedName
import quanti.com.kotlinlog.base.getWebLoggerString

data class WebLoggerEntity @JvmOverloads constructor(
        @SerializedName("sessionName") val sessionName: String,
        @SerializedName("severity") val severity: String,
        @SerializedName("message") val message: String,
        @SerializedName("id") val id: String = "",
        @SerializedName("timestamp") val timestamp: Long = System.currentTimeMillis()
) {

    @JvmOverloads constructor(
            sessionName: String,
            severity: Int,
            message: String,
            id: String = "",
            timestamp: Long = System.currentTimeMillis()) : this(sessionName, severity.getWebLoggerString(), message, id, timestamp)

    companion object {
        fun getTestEntity(sessionName: String): WebLoggerEntity = WebLoggerEntity(sessionName, "INFO", "Connection to WebLogger has been initialized.")

        fun getTestEntityList(sessionName: String) = arrayListOf<WebLoggerEntity>(getTestEntity(sessionName))
    }
}


