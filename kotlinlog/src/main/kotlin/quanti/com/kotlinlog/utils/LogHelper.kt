package quanti.com.kotlinlog.utils

import quanti.com.kotlinlog.DEBUG_LIBRARY
import quanti.com.kotlinlog.TAG

fun loga(vararg strings: Any){
    loga(strings.joinToString { it.toString() })
}

fun loga(string: String){
    if(DEBUG_LIBRARY){
        println("$TAG $string")
        android.util.Log.i(TAG, string)
    }

}