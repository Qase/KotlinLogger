package quanti.com.kotlinlog.utils

import quanti.com.kotlinlog.BuildConfig
import quanti.com.kotlinlog.TAG

fun loga(vararg strings: Any){
    loga(strings.joinToString { it.toString() })
}

fun loga(string: String){
    if(BuildConfig.DEBUG_LOG){
        //println("$TAG $string")
        android.util.Log.i(TAG, string)
    }

}