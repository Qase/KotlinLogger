package quanti.com.kotlinlog.weblogger.entity

import com.google.gson.annotations.SerializedName

data class WebLoggerEntity(
        @SerializedName("id") val id: String,
        @SerializedName("sessionName") val sessionName: String,
        @SerializedName("timestamp") val timestamp: Long,
        @SerializedName("severity") val severity: String,
        @SerializedName("message") val message: String
)
