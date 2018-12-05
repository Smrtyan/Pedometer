package com.example.ubnt.pedometer

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import org.eazegraph.lib.models.BarModel
import org.eazegraph.lib.charts.BarChart
import org.eazegraph.lib.communication.IOnBarClickedListener
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.hardware.SensorEventListener
import java.text.SimpleDateFormat
import android.hardware.SensorEvent
import android.util.Log
import android.widget.TextView
import java.util.*




class MainActivity : AppCompatActivity() {
    lateinit var sensorManager:SensorManager
    lateinit var  stepCounter:Sensor;//步伐总数传感器
    lateinit var  stepDetector: Sensor;//单次步伐传感器
    lateinit var stepCounterListener:SensorEventListener ;//步伐总数传感器事件监听器
    lateinit var stepDetectorListener:SensorEventListener ;//单次步伐传感器事件监听器
    lateinit var dbHelper: SimpleDBHelper
    var steps:Int = 0
    var totalSteps =0

    lateinit var textViewCount :TextView
    lateinit var textViewLabel :TextView
    internal val DB_VERSION = 1
    lateinit var db: SQLiteDatabase
     val chartColor = intArrayOf(
         -0xedcbaa,-0xcbcbaa,-0xa9cbaa,-0x78c0aa,-0xa9480f,-0xcbcbaa,-0xe00b54,-0xe45b1a
     )
    var colorIndex = 0
    fun getColor():Int{

        return chartColor[(colorIndex++)%chartColor.size]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper = SimpleDBHelper(this, DB_VERSION)
        val mBarChart = findViewById<View>(R.id.barchart) as BarChart

        textViewCount = findViewById<TextView>(R.id.textview_step_count)
        textViewLabel = findViewById<TextView>(R.id.textview_step_label)
        db = getDB()
       // val whereclause = "day > ?" + " and day < ?" + "and isHidden = 0"
        var cursor = db.query(
            "step", null,
            null,
            null, null, null, "day"
        )
        if (cursor.moveToFirst()) {

            do {
                val mDay =cursor.getString(cursor.getColumnIndex("day"))
                val mStep = cursor.getString(cursor.getColumnIndex("stepCount"))
                totalSteps+=mStep.toInt()
                mBarChart.addBar(BarModel(mDay,mStep.toFloat(), getColor()))
            } while (cursor.moveToNext())
            cursor.close()
            val tv_total = findViewById<TextView>(R.id.textView_total)
            tv_total.setText(""+totalSteps+"\t\tsteps")
        }

        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        val yesterday = SimpleDateFormat("yyyy-MM-dd").format(cal.time)
        cursor = db.query(
            "step", null,
            "day='"+yesterday+"'",
            null, null, null, null
        )
        if (cursor.moveToFirst()) {
            val mStep = cursor.getString(cursor.getColumnIndex("stepCount"))
            val tv_yesterDay =findViewById<TextView>(R.id.textView_yesterday)
            tv_yesterDay.setText(mStep+"\t\tsteps")
        }
        cursor.close()

        db.close()

        mBarChart.startAnimation()
        mBarChart.onBarClickedListener =
                IOnBarClickedListener {
//                    Toast.makeText(applicationContext,"Hello",Toast.LENGTH_LONG).show()
                    val intent:Intent =Intent();
                    intent.setClass(this,AchieveActivity().javaClass)
                    intent.putExtra("totalSteps",""+totalSteps);
                    startActivity(intent);
                }


        initData()
        initListener()

    }
    override fun onPause() {
        super.onPause()
        unregisterSensor()
        db = getDB()
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val curDate = Date(System.currentTimeMillis())

        val  day = formatter.format(curDate)
        var contentValues = ContentValues()

        contentValues = ContentValues()
        if(steps!=0) {
            contentValues.put("stepCount", steps)
            val whereClause = "day = '" + day + "'"

            val affectRow = db.update(
                "step",
                contentValues, whereClause,
                null
            )
            if (0 == affectRow) {
                contentValues.put("day", day)
                db.insert("step", null, contentValues)
            }
        }
        db.close()


    }

    override fun onResume() {
        super.onResume()
        registerSensor()

    }

    private fun registerSensor() {
        //注册传感器事件监听器
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER) && packageManager.hasSystemFeature(
                PackageManager.FEATURE_SENSOR_STEP_DETECTOR
            )
        ) {
            sensorManager.registerListener(stepDetectorListener, stepDetector, SensorManager.SENSOR_DELAY_FASTEST)
            sensorManager.registerListener(stepCounterListener, stepCounter, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    private fun unregisterSensor() {
        //解注册传感器事件监听器
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER) && packageManager.hasSystemFeature(
                PackageManager.FEATURE_SENSOR_STEP_DETECTOR
            )
        ) {
            sensorManager.unregisterListener(stepCounterListener)
            sensorManager.unregisterListener(stepDetectorListener)
        }
    }







    protected fun initData() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager//获取传感器系统服务
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)//获取计步总数传感器
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)//获取单次计步传感器

//        isSupportStepCounter.setText("是否支持StepCounter:" + packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER).toString())
//        isSupportStepDetector.setText("是否支持StepDetector:" + packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR).toString())

    }

    protected fun initListener() {
        stepCounterListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                Log.e(
                    "Counter-SensorChanged",
                    event.values[0].toString() + "---" + event.accuracy + "---" + event.timestamp
                )
                steps = event.values[0].toInt()
                textViewCount.setText(""+steps)
                if (0 != steps )
                    textViewLabel.setText("steps")

            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                Log.e("Counter-Accuracy", sensor.name + "---" + accuracy)

            }
        }

        stepDetectorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                Log.e(
                    "Detector-SensorChanged",
                    event.values[0].toString() + "---" + event.accuracy + "---" + event.timestamp
                )
//                stepDetectorText.setText("当前步伐计数:" + event.values[0])
//                stepDetectorTimeText.setText("当前步伐时间:" + simpleDateFormat.format(event.timestamp / 1000000))
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                Log.e("Detector-Accuracy", sensor.name + "---" + accuracy)

            }
        }
    }

    fun getDB(): SQLiteDatabase {
        return dbHelper.getWritableDatabase()
    }
}
