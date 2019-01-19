package quanti.com.kotlinlog.weblogger.entity

import com.google.gson.annotations.SerializedName

data class WebLoggerEntity @JvmOverloads constructor(
        @SerializedName("sessionName") val sessionName: String,
        @SerializedName("severity") val severity: String,
        @SerializedName("message") val message: String,
        @SerializedName("id") val id: String = "",
        @SerializedName("timestamp") val timestamp: Long = System.currentTimeMillis()
)
