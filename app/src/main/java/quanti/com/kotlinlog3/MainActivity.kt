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
import quanti.com.kotlinlog.Log
import quanti.com.kotlinlog.android.AndroidLogger
import quanti.com.kotlinlog.base.LogLevel
import quanti.com.kotlinlog.crashlytics.CrashlyticsLogger
import quanti.com.kotlinlog.file.FileLogger
import quanti.com.kotlinlog.file.SendLogDialogFragment


class MainActivity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener {


    internal var checked = 3
    internal var MY_PERMISSIONS_REQUEST = 98

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
                    MY_PERMISSIONS_REQUEST)
        } else {
            Log.addLogger(FileLogger(applicationContext))
        }

        Log.useUncheckedErrorHandler()
        Log.addLogger(AndroidLogger())

        Fabric.with(this, Crashlytics())
        Log.addLogger(CrashlyticsLogger())

        (findViewById(R.id.radio_group) as RadioGroup).setOnCheckedChangeListener(this)


    }


    fun button_clicked(view: View) {
        when (checked) {
            LogLevel.DEBUG -> Log.d("laisdjlakdj")
            LogLevel.ERROR -> Log.e("laisdjlakdj")
            LogLevel.INFO -> Log.i("laisdjlakdj")
            LogLevel.VERBOSE -> Log.v("laisdjlakdj")
            LogLevel.WARN -> Log.w("laisdjlakdj")
        }
    }

    fun button_clicked_throw(view: View) {
        Log.e("Something bad happened", ArrayIndexOutOfBoundsException("Message in exception"))
    }

    override fun onCheckedChanged(group: RadioGroup, @IdRes checkedId: Int) {
        val text = (group.findViewById(checkedId) as RadioButton).text
        checked = Integer.parseInt(text[text.length - 1] + "")
    }

    fun burst_clicked(view: View) {

        val end = 50

        Flowable
                .range(0, end)
                .map { it.toString() }
                .subscribe { Log.i(it) }
    }

    fun burst_thread_clicked(view: View) {

        val threads = 5
        val end = 1000
        (0..threads)
                .onEach {
                    val thread = it
                    Flowable.range(0, end)
                            .subscribeOn(Schedulers.newThread())
                            .subscribe { Log.i("Thread $thread\t Log: $it") }
                }

    }

    fun cz_clicked(view: View) {
        SendLogDialogFragment.newInstance("kidal5@centrum.cz").show(supportFragmentManager, "OMG")
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == MY_PERMISSIONS_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i("Diky za permission")
            Log.addLogger(FileLogger(applicationContext))
        } else {
            //show some shit
            AlertDialog.Builder(this)
                    .setTitle("</3")
                    .setMessage("Give me permission omg")
                    .setOnDismissListener {
                        Log.i("After dialog")
                        ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                MY_PERMISSIONS_REQUEST)
                    }
                    .create()
                    .show()
        }
    }

    fun test_1(view: View) {
        Toast.makeText(this, "Does nothing", Toast.LENGTH_SHORT).show()
    }


    fun test_2(view: View) {
        Flowable.range(0, 10000)
                .subscribe {
                    if (it % 1000 == 0) {
                        Log.e("Something bad happened $it", ArrayIndexOutOfBoundsException("Message in exception $it"))
                    }
                    Log.i(it.toString())
                }

    }

    fun test_3(view: View) {
        //first log to sync logger

        Flowable.range(0, 10000)
                .subscribeOn(Schedulers.newThread())
                .subscribe {
                    if (it % 1000 == 0) {
                        Log.e("Something bad happened $it", ArrayIndexOutOfBoundsException("Message in exception $it"))
                    }
                    Log.i(it.toString())
                }

        Flowable.range(0, 10000)
                .subscribeOn(Schedulers.newThread())
                .subscribe {
                    if (it % 1000 == 0) {
                        Log.e("Something bad happened $it", ArrayIndexOutOfBoundsException("Message in exception $it"))
                    }
                    Log.i(it.toString())
                }

        Flowable.range(0, 10000)
                .subscribeOn(Schedulers.newThread())
                .subscribe {
                    if (it % 1000 == 0) {
                        Log.e("Something bad happened $it", ArrayIndexOutOfBoundsException("Message in exception $it"))
                    }
                    Log.i(it.toString())
                }

    }

    fun test_4(view: View) {
        throw ArrayIndexOutOfBoundsException("unchecked exception")
    }


}










