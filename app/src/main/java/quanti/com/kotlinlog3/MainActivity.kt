@file:Suppress("UNUSED_PARAMETER", "FunctionName")

package quanti.com.kotlinlog3

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import quanti.com.kotlinlog.android.AndroidLogger
import quanti.com.kotlinlog.base.LogLevel
import quanti.com.kotlinlog.crashlytics.CrashlyticsLogger
import quanti.com.kotlinlog.file.FileLogger
import quanti.com.kotlinlog.file.SendLogDialogFragment
import quanti.com.kotlinlog.file.base.FileLoggerBundle

const val REQUEST = 98

class MainActivity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener {

    private var checked = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //resolve permission
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        // Here, thisActivity is the current activity
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST)
        } else {
            FileLogger.init(applicationContext, FileLoggerBundle())
        }




        quanti.com.kotlinlog.Log.useUncheckedErrorHandler()
        quanti.com.kotlinlog.Log.addLogger(AndroidLogger)

        Fabric.with(this, Crashlytics())
        quanti.com.kotlinlog.Log.addLogger(CrashlyticsLogger)


        (findViewById<RadioGroup>(R.id.radio_group)).setOnCheckedChangeListener(this)



    }


    fun button_clicked(view: View) {
        when (checked) {
            LogLevel.DEBUG -> quanti.com.kotlinlog.Log.d("laisdjlakdj")
            LogLevel.ERROR -> quanti.com.kotlinlog.Log.e("laisdjlakdj")
            LogLevel.INFO -> quanti.com.kotlinlog.Log.i("laisdjlakdj")
            LogLevel.VERBOSE -> quanti.com.kotlinlog.Log.v("laisdjlakdj")
            LogLevel.WARN -> quanti.com.kotlinlog.Log.w("laisdjlakdj")
        }
    }

    fun button_clicked_throw(view: View) {
        quanti.com.kotlinlog.Log.e("Something bad happened", ArrayIndexOutOfBoundsException("Message in exception"))
    }

    override fun onCheckedChanged(group: RadioGroup, @IdRes checkedId: Int) {

        val text = group.findViewById<RadioButton>(checkedId).text
        checked = Integer.parseInt(text[text.length - 1] + "")
    }

    fun burst_clicked(view: View) {

        val end = 50

        Flowable
                .range(0, end)
                .map { it.toString() }
                .subscribe { quanti.com.kotlinlog.Log.i(it) }
    }

    fun burst_thread_clicked(view: View) {

        val threads = 5
        val end = 1000
        (0..threads)
                .onEach {
                    val thread = it
                    Flowable.range(0, end)
                            .subscribeOn(Schedulers.newThread())
                            .subscribe { quanti.com.kotlinlog.Log.i("Thread $thread\t Log: $it") }
                }

    }

    fun cz_clicked(view: View) {
        SendLogDialogFragment.newInstance("kidal5@centrum.cz", deleteLogs = true).show(supportFragmentManager, "OMG")
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            quanti.com.kotlinlog.Log.i("Diky za permission")
            quanti.com.kotlinlog.Log.addLogger(FileLogger)
            FileLogger.init(applicationContext)
        } else {
            //show some shit
            AlertDialog.Builder(this)
                    .setTitle("</3")
                    .setMessage("Give me permission omg")
                    .setOnDismissListener {
                        quanti.com.kotlinlog.Log.i("After dialog")
                        ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                REQUEST)
                    }
                    .create()
                    .show()
        }
    }

    fun test_1(view: View) {
        FileLogger.deleteAllLogs()
        Toast.makeText(this, "Logs deleted", Toast.LENGTH_SHORT).show()
    }


    fun test_2(view: View) {
        Flowable.range(0, 10000)
                .subscribe {
                    if (it % 1000 == 0) {
                        quanti.com.kotlinlog.Log.e("Something bad happened $it", ArrayIndexOutOfBoundsException("Message in exception $it"))
                    }
                    quanti.com.kotlinlog.Log.i(it.toString())
                }
    }

    fun test_3(view: View) {
        //first log to sync logger

        Flowable.range(0, 10000)
                .subscribeOn(Schedulers.newThread())
                .subscribe {
                    if (it % 1000 == 0) {
                        quanti.com.kotlinlog.Log.e("Something bad happened $it", ArrayIndexOutOfBoundsException("Message in exception $it"))
                    }
                    quanti.com.kotlinlog.Log.i(it.toString())
                }

        Flowable.range(0, 10000)
                .subscribeOn(Schedulers.newThread())
                .subscribe {
                    if (it % 1000 == 0) {
                        quanti.com.kotlinlog.Log.e("Something bad happened $it", ArrayIndexOutOfBoundsException("Message in exception $it"))
                    }
                    quanti.com.kotlinlog.Log.i(it.toString())
                }

        Flowable.range(0, 10000)
                .subscribeOn(Schedulers.newThread())
                .subscribe {
                    if (it % 1000 == 0) {
                        quanti.com.kotlinlog.Log.e("Something bad happened $it", ArrayIndexOutOfBoundsException("Message in exception $it"))
                    }
                    quanti.com.kotlinlog.Log.i(it.toString())
                }

    }

    fun test_4(view: View) {
        throw ArrayIndexOutOfBoundsException("unchecked exception")
    }

    fun test_5(view: View) {
        quanti.com.kotlinlog.Log.logMetadata(applicationContext)
    }

}










