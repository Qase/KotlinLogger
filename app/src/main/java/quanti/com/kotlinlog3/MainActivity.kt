@file:Suppress("UNUSED_PARAMETER", "FunctionName")

package quanti.com.kotlinlog3

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import quanti.com.kotlinlog.Log
import quanti.com.kotlinlog.android.AndroidLogger
import quanti.com.kotlinlog.base.LogLevel
import quanti.com.kotlinlog.crashlytics.CrashlyticsLogger
import quanti.com.kotlinlog.file.FileLogger
import quanti.com.kotlinlog.file.bundle.BaseBundle
import quanti.com.kotlinlog.file.bundle.CircleLogBundle
import quanti.com.kotlinlog.file.bundle.DayLogBundle
import quanti.com.kotlinlog.file.bundle.StrictCircleLogBundle

const val REQUEST = 98

class MainActivity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener {

    private var checked = 3
    private var bundle: BaseBundle = DayLogBundle()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initLog()

        (findViewById<RadioGroup>(R.id.radioGroup)).setOnCheckedChangeListener(this)

    }

    private fun initLog() {
        Log.removeAllLoggers()
        Log.addLogger(FileLogger(applicationContext, bundle))

        Log.useUncheckedErrorHandler()
        Log.addLogger(AndroidLogger())

        Fabric.with(this, Crashlytics())
        Log.addLogger(CrashlyticsLogger())

        val text = bundle.javaClass.simpleName.replace("Bundle", "")

        (findViewById<TextView>(R.id.loggerInUseTextView)).text = text

    }

    override fun onCheckedChanged(group: RadioGroup, @IdRes checkedId: Int) {
        val text = group.findViewById<RadioButton>(checkedId).text
        checked = Integer.parseInt(text[text.length - 1] + "")
    }

    fun hitme_clicked(view: View) {
        when (checked) {
            LogLevel.DEBUG -> Log.d("laisdjlakdj")
            LogLevel.ERROR -> Log.e("laisdjlakdj")
            LogLevel.INFO -> Log.i("laisdjlakdj")
            LogLevel.VERBOSE -> Log.v("laisdjlakdj")
            LogLevel.WARN -> Log.w("laisdjlakdj")
        }
    }

    @SuppressLint("CheckResult")
    fun testone_clicked(view: View) {
        val end = 50

        Flowable
                .range(0, end)
                .map { it.toString() }
                .subscribe { Log.i(it) }


    }

    fun testtwo_clicked(view: View) {

        val threads = 5
        val end = 1000
        (0..threads)
                .onEach { thread ->
                    Flowable.range(0, end)
                            .subscribeOn(Schedulers.newThread())
                            .subscribe { Log.i("Thread $thread\t Log: $it") }
                }

    }

    @SuppressLint("CheckResult")
    fun testthree_clicked(view: View) {
        Flowable.range(0, 10000)
                .subscribe {
                    if (it % 1000 == 0) {
                        Log.e("Something bad happened $it", ArrayIndexOutOfBoundsException("Message in exception $it"))
                    }
                    Log.i("Log: $it")
                }
    }

    fun testfour_clicked(view: View) {
        (1..3).forEach { thread ->
            Flowable.range(0, 10000)
                    .subscribeOn(Schedulers.newThread())
                    .subscribe {
                        if (it % 1000 == 0) {
                            Log.e("Something bad happened $thread $it", ArrayIndexOutOfBoundsException("Message in exception $it"))
                        }
                        Log.i("Thread $thread\t Log: $it")
                    }
        }
    }

    fun throwException_clicked(view: View) {
        Log.e("Something bad happened", ArrayIndexOutOfBoundsException("Message in exception"))
    }

    fun throwUncheckedException_clicked(view: View) {
        throw ArrayIndexOutOfBoundsException("unchecked exception")
    }

    fun logMetadata_clicked(view: View) {
        Log.logMetadata(applicationContext)
    }


    fun send_clicked(view: View) {
//        SendLogDialogFragment.newInstance("kidal5@centrum.cz", deleteLogs = true).show(supportFragmentManager, "OMG")
    }

    fun deleteLogs_clicked(view: View) {
        FileLogger.deleteAllLogs(applicationContext)
        Toast.makeText(this, "Logs deleted", Toast.LENGTH_SHORT).show()
    }

    fun switchLogger_clicked(view: View) {
        bundle = when (bundle) {
            is DayLogBundle -> CircleLogBundle(maxFileSizeMegaBytes = 1)
            is CircleLogBundle -> StrictCircleLogBundle(maxFileSizeMegaBytes = 1)
            is StrictCircleLogBundle -> DayLogBundle()
            else -> throw Exception("Unknown bundle, should not arise at all")
        }

        initLog()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

//        if (requestCode == REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            Log.i("Diky za permission")
//            Log.addLogger(FileLogger)
//            FileLogger.init(applicationContext)
//        } else {
//            //show some shit
//            AlertDialog.Builder(this)
//                    .setTitle("</3")
//                    .setMessage("Give me permission omg")
//                    .setOnDismissListener {
//                        Log.i("After dialog")
//                        ActivityCompat.requestPermissions(
//                                this,
//                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                                REQUEST)
//                    }
//                    .create()
//                    .show()
//        }
    }

}










